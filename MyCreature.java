
import cosc343.assig2.Creature;
import java.util.Random;
import java.lang.*;
import java.util.ArrayList;

/**
 * The MyCreate extends the cosc343 assignment 2 Creature. Here you implement
 * creatures chromosome and the agent function that maps creature percepts to
 * actions.
 *
 * @author
 * @version 1.0
 * @since 2017-04-05
 */
public class MyCreature extends Creature {

    Random rand = new Random();
    public float[][] chromosome;
    public int numChromosome;
    public int numChrom2;


    public MyCreature(int numPercepts, int numActions) {
        numChrom2 = numActions;
        numChromosome = (numPercepts *4) +1;
        chromosome = new float[numChromosome][numChrom2];
        for (int i = 0; i < numChromosome; i++) {
            for (int x = 0; x < numChrom2; x++) {
                chromosome[i][x] = (float) rand.nextGaussian();
            }
        }
    }

    private float[] softmax(float[] inputs) {
        double sum = 0;

        for (float value : inputs) {
            sum += Math.exp(value);
        }

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = (float) (Math.exp(inputs[i]) / sum);
        }

        return inputs;
    }

    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {
        float[] actions = new float[numExpectedActions];
        int[] input = new int[36];
        for (int i = 0; i < numPercepts; i++) {
            input[i * 4 + percepts[i]] = 1;
        }
        float[] output = new float[11];
        for (int i = 0; i < 11; i++) {
            for (int x = 0; x < 36; x++) {
                float weight = chromosome[x][i];
                output[i] += input[x] * weight;
            }
            output[i] +=  chromosome[36][i];
        }
        output = softmax(output);
        
        for (int i = 0; i < numExpectedActions; i++) {
            actions[i] = (float) output[i];
        }
        return actions;
    }
}
