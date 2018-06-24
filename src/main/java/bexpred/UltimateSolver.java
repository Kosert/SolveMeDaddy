package bexpred;

import helpers.CNFValue;
import helpers.Permutation;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UltimateSolver {

    private ISolver solver;
    private ModelIterator mi;
    private Reader reader;
    private IProblem problem;
    private Map<String, String> predefinedValues;
    private final String PERMS = "permutations.txt";
    private final String PERMS_REV = "permutations_rev.txt";
    private BufferedWriter writer;

    public UltimateSolver() {
        solver = SolverFactory.newDefault();
        mi = new ModelIterator(solver);
        solver.setTimeout(3600); // 1h
        reader = new DimacsReader(mi);
    }

    private List<String> getVariables(Collection<String> expressions) throws BExprPreParseException {
        String expression = "";

        int i = 0;
        for(String exp : expressions) {
            expression += exp;
            i++;
            if(i < expressions.size())
                expression += " | ";
        }

        return new BExprTree(expression).getVars();
    }

    private String getCNF(String expression) throws BExprPreParseException {
        BExprTree tmpTree = new BExprTree(expression);

        String pos = tmpTree.getTruthTable().getPOS(tmpTree.getVars());
        pos = pos.replaceAll("\\*", " 0\n");
        pos = pos.replaceAll(" \\+ ", " ");
        pos = pos.replaceAll("!", "-");
        pos = pos.replaceAll("[()]", "");
        return pos + " 0";
    }

    public List<Map<String, String>> getAllSolutions(Map<String, String> outs, Map<String, Boolean> outsValues, Map<String, Boolean> inputsValues)
            throws BExprPreParseException {
        resetFile(PERMS);
        resetFile(PERMS_REV);
        if(outs == null || outs.isEmpty())
            return new ArrayList<Map<String, String>>();

        predefinedValues = inputsValues.keySet().stream().collect(Collectors.toMap(s -> s, s -> inputsValues.get(s) ? "1" : "0"));

//        Step 1. Map names into values
        List<String> variables = getVariables(outs.values());
        variables.sort(Comparator.comparingInt(String::length).reversed());

        Map<String, Integer> variablesMap = new HashMap<>();
        Map<String, String> reversedVariablesMap = new HashMap<>();
        Map<String, Boolean> inpValues = new HashMap<>();
        for(int i = 0; i < variables.size(); i++) {
            variablesMap.put(variables.get(i), i + 1);
            reversedVariablesMap.put("" + (i + 1), variables.get(i));
            if(inputsValues.get(variables.get(i)) != null)
                inpValues.put("" + (i + 1), inputsValues.get(variables.get(i)));
        }

//        Step 2. Get CNFs and replace values names with mapped values
        List<CNFValue> cnfValues = new ArrayList<>();
        for(String key : outs.keySet()) {
            String out1 = outs.get(key);
            String out0 = "!(" + outs.get(key)+ ")";
            out1 = getCNF(out1);
            out0 = getCNF(out0);
            for(String variable : variables) {
                out1 = out1.replaceAll(variable, variablesMap.get(variable).toString());
                out0 = out0.replaceAll(variable, variablesMap.get(variable).toString());
            }

            if(outsValues.get(key) == null) {
                cnfValues.add(new CNFValue(key, false, out0, inpValues));
                cnfValues.add(new CNFValue(key, true, out1, inpValues));
            } else if(outsValues.get(key)) {
                cnfValues.add(new CNFValue(key, true, out1, inpValues));
            } else {
                cnfValues.add(new CNFValue(key, false, out0, inpValues));
            }
        }

//        Step 3. Generate possible permutations and solutions
        List<String> outputsNames = new ArrayList<>(outs.keySet());
        List<List<Boolean>> permutationList = getPermutations(outputsNames, outsValues);
        List<Permutation> cnfPermutations = getCNFPermutations(permutationList, outputsNames, cnfValues);

        List<Map<String,String>> solutions = getSolutions(cnfPermutations, reversedVariablesMap);

        return solutions;
    }

    private List<Permutation> getCNFPermutations(List<List<Boolean>> permutations, List<String> outNames, List<CNFValue> cnfValues) {
        List<Permutation> results = new ArrayList<>();

        for(List<Boolean> permutation : permutations) {
            String cnfPermutation = "";
            for(int i = 0; i < outNames.size(); i++) {
                cnfPermutation += getCNFValue(cnfValues, outNames.get(i), permutation.get(i)).getCnf();
            }
            results.add(new Permutation(permutation, outNames, cnfPermutation));
        }

        return results;
    }


    private List<Map<String, String>> getSolutions(List<Permutation> cnfPermutations, Map<String, String> valuesMapping) {
        List<Map<String, String>> results = new ArrayList<>();

        for(Permutation cnfPermutation : cnfPermutations) {
            if(cnfPermutation.getPermutation().length() < 3)
                continue;

            try {
                String cnf = cnfPermutation.getPermutation();
                long numberOfLines;
                String preCnf;
                String outsWithValues = "c Outputs values:\nc ";
                for(int i =0; i < cnfPermutation.getOutputsNames().size(); i++) {
                    outsWithValues += cnfPermutation.getOutputsNames().get(i) + " : " +  (cnfPermutation.getStates().get(i) ? "1" : "0") + ", ";
                }

                if(cnf.contains("#unsat")) {
                    numberOfLines = cnf.replaceAll("#unsat", "0").chars().filter(ch -> ch == '0').count();
                    preCnf = outsWithValues + "\np cnf " + valuesMapping.keySet().size() + " " + numberOfLines + "\n";

                    appendFile(PERMS,"c unsatisfiable\n" +  preCnf + cnf.substring(0, cnf.length() - 1).replaceAll("#unsat", "0"));
                    appendFile(PERMS_REV,"c unsatisfiable\n" + preCnf + replaceWithMappings(cnf.substring(0, cnf.length() - 1).replaceAll("#unsat", "0"), valuesMapping));

                    continue;
                }

                List<String> outputNames = cnfPermutation.getOutputsNames();
                List<Boolean> states = cnfPermutation.getStates();
                Map<String, String> predfValues = new HashMap<>();
                for(int i = 0; i < outputNames.size(); i++) {
                    predfValues.put(outputNames.get(i), states.get(i) ? "1" : "0");
                }
                predfValues.putAll(predefinedValues);

                if(!cnf.matches("[\\s\\r]*")) {
                    numberOfLines = cnf.chars().filter(ch -> ch == '0').count();
                    preCnf = outsWithValues + "\np cnf " + valuesMapping.keySet().size() + " " + numberOfLines + "\n";
                    problem = reader.parseInstance(new ByteArrayInputStream((preCnf + cnf.substring(0, cnf.length() - 1)).getBytes()));

                    appendFile(PERMS, preCnf + cnf.substring(0, cnf.length() - 1));
                    appendFile(PERMS_REV, preCnf + replaceWithMappings(cnf.substring(0, cnf.length() - 1), valuesMapping));

                    while (problem.isSatisfiable()) {
                        Map<String, String> cnfResult = new HashMap<>(predfValues);

                        int[] res = problem.model();
                        for (int variable : res) {
                            cnfResult.put(valuesMapping.get("" + Math.abs(variable)), (variable > 0 ? "1" : "0"));
                        }
                        cnfResult.putAll(predefinedValues);
                        results.add(cnfResult);
                    }
                } else {
                    results.add(predfValues);
                }
            } catch (ParseFormatException | TimeoutException e) {
//                e.printStackTrace();
            } catch (ContradictionException | IOException e) {
//                Inconsistency
            }
        }
        return results.stream().distinct().collect(Collectors.toList());
    }

    private String replaceWithMappings(String perm, Map<String, String> mapping) {
        String result = perm;
        List<String> keys = new ArrayList<>(mapping.keySet());
        keys.sort(Comparator.comparingInt(String::length).reversed());
        for(String key : keys) {
            result = result.replaceAll(key, mapping.get(key));
        }

        return result;
    }

    private CNFValue getCNFValue(List<CNFValue> values, String name, boolean state) {
        for(CNFValue value : values) {
            if(value.getOutputName().equals(name) && value.getState() == state)
                return value;
        }
        return null;
    }

    private List<List<Boolean>> getPermutations(List<String> outs, Map<String, Boolean> vals) {
        List<List<Boolean>> result = new ArrayList<>();

        if(outs.size() == 1) {
            if(vals.get(outs.get(0)) == null) {
                result.add(Arrays.asList(false));
                result.add(Arrays.asList(true));
            } else if(vals.get(outs.get(0))) {
                result.add(Arrays.asList(true));
            } else {
                result.add(Arrays.asList(false));
            }
            return result;
        }

        for(List<Boolean> permutation : getPermutations(outs.subList(1, outs.size()), vals)) {
            List<Boolean> falseList = new ArrayList<>(permutation);
            falseList.add(0, false);
            List<Boolean> trueList = new ArrayList<>(permutation);
            trueList.add(0, true);

            if(vals.get(outs.get(0)) == null) {
                result.add(falseList);
                result.add(trueList);
            } else if(vals.get(outs)) {
                result.add(trueList);
            } else {
                result.add(falseList);
            }
        }

        return result;
    }

    private void resetFile(String filename) {
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write("c File containing all permutations");
        } catch (Exception e) {

        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendFile(String filename, String text) {
        try {
            writer = new BufferedWriter(new FileWriter(filename, true));
            writer.append("\nc +-------------------------------------------------------------------+\n\n" + text);
        } catch (Exception e) {

        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
