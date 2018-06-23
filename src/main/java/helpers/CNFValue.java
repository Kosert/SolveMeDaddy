package helpers;

import java.util.Map;

public class CNFValue {

    private String outputName;
    private boolean state;
    private String cnf;

    public CNFValue(String outputName, boolean state, String cnf, Map<String, Boolean> values) {
        this.outputName = outputName;
        this.state = state;
        this.cnf = simplifyCNF(cnf, values);
    }

    private String simplifyCNF(String CNF, Map<String, Boolean> values) {
        String result = "";
        String[] lines = CNF.split("\n");

        for(String line : lines) {
            String toAdd = " " + line + " ";
            for(String value : values.keySet()) {
                if(values.get(value)) {
                    if(toAdd.contains(" " + value + " ")) {
                        toAdd = "";
                        break;
                    }
                    toAdd = toAdd.replaceAll("-" + value + " ", "");
                } else {
                    if(toAdd.contains("-" + value + " ")) {
                        toAdd = "";
                        break;
                    }
                    toAdd = toAdd.replaceAll(" " + value + " ", " ");
                }
            }

            toAdd = toAdd.replaceAll("^ [ \t]*0 *$", "#unsat");
            if(!toAdd.equals(""))
                result += toAdd + "\n";
        }

        return result;
    }

    public String getOutputName() {
        return outputName;
    }

    public boolean getState() {
        return state;
    }

    public String getCnf() {
        return cnf;
    }
}
