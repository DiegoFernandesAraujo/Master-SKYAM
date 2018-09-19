/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dedupalgorithms.cds;

import dedupalgorithms.DedupAlg;
import dude.algorithm.Algorithm;
import dude.datasource.CSVSource;
import dude.output.CSVOutput;
import dude.output.DuDeOutput;
import dude.output.JsonOutput;
import dude.output.statisticoutput.CSVStatisticOutput;
import dude.output.statisticoutput.SimpleStatisticOutput;
import dude.output.statisticoutput.StatisticOutput;
import dude.postprocessor.NaiveTransitiveClosureGenerator;
import dude.postprocessor.StatisticComponent;
import dude.postprocessor.WarshallTransitiveClosureGenerator;
import dude.preprocessor.Preprocessor;
import dude.similarityfunction.SimilarityFunction;
import dude.similarityfunction.aggregators.Aggregator;
import dude.similarityfunction.aggregators.Average;
import dude.similarityfunction.contentbased.impl.SoundExFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.JaccardSimilarityFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.LevenshteinDistanceFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.MongeElkanFunction;
import dude.similarityfunction.contentbased.impl.simmetrics.SimmetricsFunction;
import dude.util.GoldStandard;
import dude.util.data.DuDeObjectPair;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Diego
 */
public class Alg19 extends DedupAlg {

    FileWriter escreveResult;
    File estatisticasCSV;
    File estatisticasTXT;
    String dir = "resultsDedup/cds";

    public Alg19(String baseDados1, String chavePrimaria, String gold, String goldId1, String goldId2, int ordem) {
        super(baseDados1, chavePrimaria, gold, goldId1, goldId2, ';');

        estatisticasCSV = new File("./src/csv/" + dir + "/estatisticas", "estatisticasDedup" + ordem + ".csv");
        estatisticasTXT = new File("./src/csv/" + dir + "/estatisticas", "estatisticasDedup" + ordem + ".txt");

        if (estatisticasTXT.exists() | estatisticasCSV.exists()) {
            System.out.println("Já existem resultados para esse algoritmo!");
            java.awt.Toolkit.getDefaultToolkit().beep();
            System.exit(0);
        }

        try {
            this.escreveResult = new FileWriter(new File("./src/csv/" + dir, "resultado" + ordem + ".csv"));

        } catch (IOException ex) {
            Logger.getLogger(Alg14.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    File resultado = new File("./src/csv/datasets", "resultado.csv");
    @Override
    public void executaDedupAlg() throws IOException {

        GoldStandard goldStandard = getGS();

        Algorithm algorithm = getAlg();
        algorithm.enableInMemoryProcessing();

        SoundExFunction similarityFunc2 = new SoundExFunction ("artist");
        MongeElkanFunction similarityFunc = new MongeElkanFunction ("title");
        LevenshteinDistanceFunction similarityFunc3 = new LevenshteinDistanceFunction("track01");
        LevenshteinDistanceFunction similarityFunc4 = new LevenshteinDistanceFunction("track02");
        LevenshteinDistanceFunction similarityFunc5 = new LevenshteinDistanceFunction("track10");
        LevenshteinDistanceFunction similarityFunc6 = new LevenshteinDistanceFunction("track11");
        

//        DuDeOutput output = new JsonOutput(System.out);
        DuDeOutput output = new CSVOutput(escreveResult);

        StatisticComponent statistic = new StatisticComponent(goldStandard, algorithm);

        StatisticOutput statisticOutputCSV;
        StatisticOutput statisticOutputTXT;

        statisticOutputCSV = new CSVStatisticOutput(estatisticasCSV, statistic, ';');
        statisticOutputTXT = new SimpleStatisticOutput(estatisticasTXT, statistic);

//        statisticOutput = new SimpleStatisticOutput(System.out, statistic);
        statistic.setStartTime();

        NaiveTransitiveClosureGenerator fechoTrans = new NaiveTransitiveClosureGenerator();

        //Gerando o fecho transitivo
        //Utilização de pesos diversos para os atributos
        for (DuDeObjectPair pair : algorithm) {
            final double similarity = similarityFunc.getSimilarity(pair)*1;
            final double similarity2 = similarityFunc2.getSimilarity(pair)*2;
            final double similarity3 = similarityFunc3.getSimilarity(pair)*0.8;
            final double similarity4 = similarityFunc4.getSimilarity(pair)*0.8;
            final double similarity5 = similarityFunc5.getSimilarity(pair)*0.9;
            final double similarity6 = similarityFunc6.getSimilarity(pair)*0.9;
            
            if ( (similarity + similarity2 + similarity3  + similarity4 + similarity5 + similarity6)/6.4 >= 0.8) {
                fechoTrans.add(pair);

            } else {
                statistic.addNonDuplicate(pair);
            }
        }

        
        for (DuDeObjectPair pair : fechoTrans) {

            statistic.addDuplicate(pair);
            output.write(pair);

        }

        statistic.setEndTime();

        statisticOutputCSV.writeStatistics();
        statisticOutputTXT.writeStatistics();

        algorithm.cleanUp();
        goldStandard.close();

    }

    public static void main(String[] args) {
        Alg19 obj1 = new Alg19("cd", "pk", "cd_gold", "disc1_id", "disc2_id", 19);
        try {
            obj1.executaDedupAlg();
        } catch (IOException ex) {
            Logger.getLogger(Alg19.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    

}