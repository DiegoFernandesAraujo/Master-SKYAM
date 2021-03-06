/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DS;

import AS.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Diego
 */
public class AplicacaoDS {

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        DgStd obj = new DgStd();
        

        int qtdAlg = 23; //Quantidade de algoritmos de resolução de entidades não supervisionados utilizados no processo

        File gs = new File("./src/csv/datasets", "cd_gold.csv");
        
        DgStd1 obj = new DgStd1(gs);
        AnnStd obj1 = new AnnStd(gs);

        obj.setGs(gs);

        obj.setDedup(true);
//        obj.setDedup(false);

        obj.setTamBaseOrig(9763); //Necessário!

        obj1.setGs(gs);

//        obj1.setDedup(true);
//        obj.setDedup(false);
        obj1.setTamBaseOrig(9763); //Necessário!

        File[] resultados = new File[qtdAlg];
        for (int i = 0; i < resultados.length; ++i) {
            int index = i + 1;
            resultados[i] = new File("./src/csv/resultsDedup", "resultado" + index + ".csv");
        }

        //Padronização dos arquivos
        File[] resultadosPadr = new File[qtdAlg];

        for (int i = 0; i < resultadosPadr.length; ++i) {
//            resultadosPadr[i] = obj.padronizaCsvFile(resultados[i]);
            resultadosPadr[i] = obj1.padronizaCsvFile(resultados[i]);
        }

        List<String> aux = new ArrayList<String>();
//        Random gerador = new Random(); //Pode ser desconsiderado, dado que a ordem aqui não importa

        //1000 experimentos aleatórios
//        for (int i = 1; i <= 1000; i++) {
        for (int i = 1; i <= 1; i++) {

            obj.setPermutacao(i);
            obj.limpaTudo();
            System.out.println("Iteração " + i);

            int cont = 0;

            while (aux.size() < qtdAlg) {

//                int randomNum = gerador.nextInt(resultados.length);
//
//                if (!aux.contains(Integer.toString(randomNum))) {
//
//                    aux.add(Integer.toString(randomNum));
//                    obj.comparaConjuntos(resultadosPadr[randomNum]);
//
//                }
                aux.add(Integer.toString(cont));

//                if (aux.size() == qtdAlg - 1) { //Gerar estatísticas só na última iteração
                if (aux.size() == qtdAlg) { //Gerar estatísticas só na última iteração
                    obj.setGeraEst(true);
                }

                System.out.println(resultadosPadr[cont].getName());

                obj.comparaConjuntos(resultadosPadr[cont]);

                cont++;

            }

//QUANDO TIVER OS ARQUIVOS COM VALORES DE SIMILARIDADE
            {
                obj.contabilizaEstatDA(obj.getHistoricoDA());
                obj.contabilizaEstatNAODA(obj.getHistoricoNAODA());

                obj.filtraDivergencias_NEW(obj.getEstatDA(), obj.getEstatNAODA());
                
                obj.incrementaEstatNAO_DA();
            }
            //Impressão dos algoritmos utilizados
            Iterator it = aux.iterator();

            while (it.hasNext()) {
                System.out.print(it.next() + ", ");
            }

            aux.clear();

        }

//        obj.remDupDiverg();
        java.awt.Toolkit.getDefaultToolkit().beep();

    }

}
