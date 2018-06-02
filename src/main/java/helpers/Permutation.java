package helpers;

import java.util.List;

public class Permutation {

    private List<Boolean> states;
    private List<String> outputsNames;
    private String permutation;

    public Permutation(List<Boolean> states, List<String> outputsNames, String permutation) {
        this.states = states;
        this.outputsNames = outputsNames;
        this.permutation = permutation;
    }

    public List<Boolean> getStates() {
        return states;
    }

    public List<String> getOutputsNames() {
        return outputsNames;
    }

    public String getPermutation() {
        return permutation;
    }
}
