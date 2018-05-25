
import cosc343.assig2.Creature;
import cosc343.assig2.World;

import java.io.*;
import java.util.*;

/**
 * The MyWorld extends the cosc343 assignment 2 World. Here you can set some
 * variables that control the simulations and override functions that generate
 * populations of creatures that the World requires for its simulations.
 *
 * @version 1.0
 * @since 2017-04-05
 */
public class MyWorld extends World {

    private final int _numTurns = 100;
    private final int _numGenerations = 5000;
    private int generation = 0;
    private FileWriter fw = null;


    /* Constructor.
       Input: worldType - specifies which simulation will be running
              griSize - the size of the world
              windowWidth - the width (in pixels) of the visualisation window
              windowHeight - the height (in pixels) of the visualisation window
              repeatableMode - if set to true, every simulation in each
                               generation will start from the same state
     */
    public MyWorld(int worldType, int gridSize, int windowWidth, int windowHeight, boolean repeatableMode) {
        // Initialise the parent class - don't remove this
        super(worldType, gridSize, windowWidth, windowHeight, repeatableMode);

        // Set the number of turns and generations
        setNumTurns(_numTurns);
        setNumGenerations(_numGenerations);

    }

    /* The main function for the MyWorld application
     */
    public static void main(String[] args) {
        // Here you can specify the grid size, window size and whether to run
        // in repeatable mode or not
        int gridSize = 40;
        int windowWidth = 1600;
        int windowHeight = 900;
        int worldType = 2;
        new MyWorld(worldType, gridSize, windowWidth, windowHeight, false);
    }

    @Override
    public MyCreature[] firstGeneration(int numCreatures) {
        MyCreature[] population = new MyCreature[numCreatures];
        int numPercepts = expectedNumberofPercepts();
        int numActions = expectedNumberofActions();
        for (int i = 0; i < numCreatures; i++) {
            population[i] = new MyCreature(numPercepts, numActions);
        }
        try {
            File file = new File("fitnessResults");
            file.createNewFile();
            fw = new FileWriter(file);
            fw.flush();
            fw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("error in write");
        } catch (IOException ex) {
        }
        return population;
    }

    @Override
    public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {
        generation++;
        MyCreature[] old_population = (MyCreature[]) old_population_btc;
        float avgLifeTime = 0f;
        int nSurvivors = 0;
        float avgfitness = 0;
        for (MyCreature creature : old_population) {
            boolean dead = creature.isDead();
            avgfitness += value(creature);
            if (dead) {
                int timeOfDeath = creature.timeOfDeath();
                avgLifeTime += (float) timeOfDeath;
            } else {
                nSurvivors += 1;
                avgLifeTime += (float) _numTurns;
            }
        }
        avgfitness /= (float) numCreatures;
        avgLifeTime /= (float) numCreatures;

        try {
            fw = new FileWriter("fitnessResults", true);
            String text = generation + " " + avgfitness + " " + nSurvivors + " " + avgLifeTime + "\n";
            fw.append(text);
            fw.flush();
            fw.close();
        } catch (FileNotFoundException ex) {
            System.err.println("error in write");
        } catch (IOException ex) {
        }
        return makeNextGen(old_population, numCreatures);
    }

    private MyCreature[] makeNextGen(MyCreature[] creatures, int numCreatures) {
        ArrayList<MyCreature> oldGen = new ArrayList<>(Arrays.asList(creatures));
        ArrayList<MyCreature> newGen = new ArrayList<>();

        oldGen.sort(Comparator.comparingInt(this::value));

        for (int i = 0; i < (int) (numCreatures *0.1 ); i++) {
            newGen.add(oldGen.get((numCreatures - 1) - i));
        }

        while (newGen.size() < numCreatures) {
            MyCreature[] inTournment = selectTournment(creatures, 10);
            MyCreature c1 = ranTournment(inTournment, 10);
            MyCreature c2 =ranTournment(inTournment, 10);
            newGen.addAll(mixGenes(c1, c2));
        }

        while (newGen.size() > numCreatures) {
            newGen.remove(newGen.size() - 1);
        }

        return newGen.toArray(new MyCreature[0]);
    }
    
    public MyCreature[] selectTournment(MyCreature[] creatures, int size) {
        MyCreature[] tournment = new MyCreature[size];
        
        for (int i = 0; i < size; i++) {
            int r = rand.nextInt(size);
            tournment[i] = creatures[r];
        }
        return tournment;
    }

    public MyCreature ranTournment(MyCreature[] cs, int numCreature) {
        MyCreature best = cs[0];

        for (MyCreature creature : cs) {
            if (value(creature) > value(best)) {
                best = creature;
            }
        }
        MyCreature perant1 = best;
        return (perant1);
    }

    public int value(MyCreature c) {
        int result = 0;
        float t = c.timeOfDeath();
        float e = (c.getEnergy());

        if (!c.isDead()) {
            t = _numTurns;
        }
        result += t;
        result += (int) (((e * t) / _numTurns) * 100);
        return result;

    }

    public ArrayList<MyCreature> mixGenes(MyCreature p1, MyCreature p2) {
        ArrayList<MyCreature> children = new ArrayList<>();
        int numPercepts = expectedNumberofPercepts();
        int numActions = expectedNumberofActions();
        MyCreature child = new MyCreature(numPercepts, numActions);
        MyCreature child2 = new MyCreature(numPercepts, numActions);

        int crossover_index = rand.nextInt(37 * 11);
        int index1 = crossover_index / 11;
        int index2 = crossover_index % 11;
        for (int i = 0; i < index1; i++) {
            for (int x = 0; x < 11; x++) {
                child.chromosome[i][x] = p1.chromosome[i][x];
                child2.chromosome[i][x] = p2.chromosome[i][x];
            }
        }
        for (int x = 0; x <= index2; x++) {
            child.chromosome[index1][x] = p1.chromosome[index1][x];
            child2.chromosome[index1][x] = p2.chromosome[index1][x];
        }
        for (int x = index2 + 1; x > 11; x++) {
            child.chromosome[index1][x] = p1.chromosome[index1][x];
            child2.chromosome[index1][x] = p2.chromosome[index1][x];
        }
        for (int i = index1 + 1; i < 37; i++) {
            for (int x = 0; x < 11; x++) {
                child.chromosome[i][x] = p1.chromosome[i][x];
                child2.chromosome[i][x] = p2.chromosome[i][x];
            }
        }

        mutate(child);
        mutate(child2);
        children.add(child);
        children.add(child2);
        return children;
    }

    public void mutate(MyCreature c) {
        for (int gene = 0; gene < 11; gene++) {
            for (int x = 0; x < 37; x++) {
                if (rand.nextFloat() > 0.02) {
                    c.chromosome[x][gene] += (float) rand.nextGaussian();
                }
            }
        }
    }

}

