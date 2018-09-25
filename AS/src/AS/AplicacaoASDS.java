/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AS;

import DS.DgStd1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Diego
 */
public class AplicacaoASDS {

    File arqAlg = new File("./src/csv/algoritmos.csv");

    public static void main(String[] args) throws IOException, InterruptedException {
        AnnStd objAS = new AnnStd();
        DgStd1 objDS = new DgStd1();

        String abordagemAA = "Dg"; //Pt - Peter Christen ou Dg - Diego Araújo

        //Se for a abordagem de AA proposta nesse trabalho, não copia os arquivos de divergência convencionais,
        //pois será necessário copiar os arquivos de divergência com as estatísticas (méd, mín, máx).
        
        //Na verdade essa diferenciação abaixo se faz desnecessária, podendo deixar objDS.setCopiaArqDiverg(false) direto,
        // pois o algoritmo de Peter Christen permite que sejam selecionados os atributos a serem utilizados por 
        //ele em cada arquivo de divergência.
        if (abordagemAA.equals("Dg")) { 
            objDS.setCopiaArqDiverg(false);
        } else {
            objDS.setCopiaArqDiverg(true); //Pode ser descartado.
        }
        
        long seed = 500;

        //CONFIGURAÇÃO DOS DADOS REFERENTES AO EXPERIMENTO
        int qtdAlg = 23; //Quantidade de algoritmos de resolução de entidades não supervisionados utilizados no processo

        File gs = new File("./src/csv/datasets", "cd_gold.csv");

        objAS.setGs(gs);
        objDS.setGs(gs);

        //Para deduplicação
        objAS.setDedup(true);
        objDS.setDedup(true);

        //Para record linkage
//        objAS.setDedup(false);
//        objDS.setDedup(false);

        objAS.setTamBaseOrig(9763); //Necessário!
//        objAS.setTamBaseOrig2(9763); //Necessário!
        objDS.setTamBaseOrig(9763); //Necessário!
//        objAS.setTamBaseOrig2(9763); //Necessário!

        //CONFIGURAÇÃO DOS DADOS REFERENTES AO EXPERIMENTO
        File[] resultados = new File[qtdAlg];
        for (int i = 0; i < resultados.length; ++i) {
            int index = i + 1;
            //O diretório que segue abaixo tem que ser setado de acordo com a base de dados utilizada
//            resultados[i] = new File("./src/csv/resultsDedup", "resultado" + index + ".csv"); 
            resultados[i] = new File("./src/csv/resultsDedup/cds", "resultado" + index + ".csv"); 
        }

        //Padronização dos arquivos
        File[] resultadosPadr = new File[qtdAlg];

        for (int i = 0; i < resultadosPadr.length; ++i) {
            resultadosPadr[i] = objAS.padronizaCsvFile(resultados[i]);
//            resultadosPadr[i] = objDS.padronizaCsvFile(resultados[i]);
        }

//        int qtdAlg = 10; //n algoritmos
        int[] vQtdAlg = {10, 15, 20};//, 25}; //Quantidades de algoritmos para geração das observações
//        int[] vQtdAlg = {3};//, 25}; //Quantidades de algoritmos para geração das observações
//        int[] vQtdAlg = {10};//, 25}; //Quantidades de algoritmos para geração das observações

        int qtdObservacoes = 1; //Quantidade de observações a serem geradas para os experimentos (ANTES ERAM 1000)

//        File algSort3 = new File("./src/csv/", "algoritmos3.csv");
        File algSort10 = new File("./src/csv/", "algoritmos10.csv");
        File algSort15 = new File("./src/csv/", "algoritmos15.csv");
        File algSort20 = new File("./src/csv/", "algoritmos20.csv");
//        File algSort23 = new File("./src/csv/", "algoritmos23.csv");

        ArrayList<File> algSorts = new ArrayList<File>();
//        algSorts.add(algSort3);
        algSorts.add(algSort10);
        algSorts.add(algSort15);
        algSorts.add(algSort20);
//        algSorts.add(algSort23);

//        int sohParaTestar = 0;
        for (int qtdAlgUt : vQtdAlg) { //Adicionado depois

            File algSort = null;

            for (File file : algSorts) {

                if (file.getName().contains(Integer.toString(qtdAlgUt))) {

                    algSort = file; //Seleciona o arquivo com a lista de sequências aleatórias de <qtdAlgUt> algoritmos utilizados
                    break;

                }
            }

          
            System.out.println("Quantidade de algoritmos: " + qtdAlgUt);

            //Gerando observações através de seleção aleatória de n algoritmos de deduplicação
            for (int i = 1; i <= qtdObservacoes; i++) {

                ArrayList<Integer> listaAlg = geraOrdAlg(qtdAlgUt, seed, qtdAlg);
//                objAS.limpaTudo();
//                objDS.limpaTudo();

                //Verifica se a sequência gerada não já foi utilizada antes
                if (!buscaAlgoritmos(algSort, listaAlg)) {

                    gravaAlgoritmos(algSort, listaAlg);

                    objAS.setPermutacao(i);
                    objAS.setQtdAlg(qtdAlgUt);
                    objAS.limpaTudo();

                    objDS.setPermutacao(i);
                    objDS.setQtdAlg(qtdAlgUt);
                    objDS.limpaTudo();
                    System.out.println("Iteração " + i);

                    int alg = 0;

                    for (int index : listaAlg) { //Para cada algoritmo contido na lista

//                        System.out.println("AQUI");||
//                        System.out.println(index + ",");
                        alg++;

                        objAS.comparaConjuntos(resultadosPadr[index]); //Compara o algoritmo atual aos demais para gerar o arquivo de divergência

                        if (alg == listaAlg.size()) { //Gerar estatísticas só na última iteração
//                            System.out.println("último algoritmo: " + alg);
//                            System.out.println("Gerando estatísticas para a última iteração pela " + ++sohParaTestar + " vez!");
                            objDS.setGeraEst(true);
                        }
//                        System.out.println("resultadosPadr[index]: " + resultadosPadr[index].getName());
                        objDS.comparaConjuntos(resultadosPadr[index]); //Compara o algoritmo atual aos demais para gerar o arquivo de divergência
                    }

                    //QUANDO TIVER OS ARQUIVOS COM VALORES DE SIMILARIDADE
                    if (abordagemAA.equals("Dg")) {
                        
                        objDS.contabilizaEstatDA(objDS.getHistoricoDA());
                        objDS.contabilizaEstatNAODA(objDS.getHistoricoNAODA());

                        objDS.filtraDivergencias_NEW(objDS.getEstatDA(), objDS.getEstatNAODA());

                        objDS.incrementaEstatNAO_DA(); //Necessário para gerar os arquivos de divergência

                        objDS.copiaArqDivergAA(); //Deve ser obrigatorieamente chamado quando se for aplicar a estratégia
                        //de AA proposta.
                    }
//                    System.out.println("");

                } else {
//                    System.out.println("Entrou no else");
                    i--;
                }
                seed += 10;
            }
            java.awt.Toolkit.getDefaultToolkit().beep();

        }

    }

    public int getTamAlg() throws IOException {

        int tamAlg = 0;

        LineNumberReader linhaLeitura1 = null;

        try {
            linhaLeitura1 = new LineNumberReader(new FileReader(arqAlg.getPath()));
            linhaLeitura1.skip(arqAlg.length());
            tamAlg = linhaLeitura1.getLineNumber();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AnnStd.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(AnnStd.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            linhaLeitura1.close();
        }

        return tamAlg;

    }

    //Gera ordem aleatória de algoritmos sem repetição dessa
    public static ArrayList<Integer> geraOrdAlg(int qtdAlgUt, long seed, int maxAlgUt) { //Esse static precisa mesmo?

        ArrayList<Integer> aux = new ArrayList<Integer>();
        Random gerador = new Random(seed);

        while (aux.size() < qtdAlgUt) {

            int randomNum = gerador.nextInt(maxAlgUt);

            if (!aux.contains(randomNum)) {
                aux.add(randomNum);
            }

        }

//        System.out.println("Lista não ordenada");
//        
//        for (Integer valor : aux) {
//            
//            System.out.print(valor + " ");
//        }
//        
        aux.sort(null);
//        System.out.println("");
//        System.out.println("Lista ordenada");
//        
//        for (Integer valor : aux) {
//            
//            System.out.print(valor + " ");
//        }
//        System.out.println("");
//        for (Integer valor : aux) {
//            System.out.print(valor + " ");
//        }
//        System.out.println("");
        //Busca CSV
//        aux.clear();
//        }
        return aux;

    }

    //Grava uma lista de algoritmos em um arquivo dedicado a manter o histórico de algoritmos selecionados 
    //para compor a amostra para os experimentos
    public static void gravaAlgoritmos(File arqResult, ArrayList<Integer> lista) throws IOException {

        FileWriter escreveAlg = null;
        BufferedWriter bwAlg = null;

        for (Integer valor : lista) {
//            System.out.println("Oi");
            System.out.print(valor + " ");
        }
        System.out.println("");

        try {
            escreveAlg = new FileWriter(arqResult, true);
            bwAlg = new BufferedWriter(escreveAlg);

            bwAlg.write(lista + "\n");
//            for (Integer valor : lista) {
//                bwAlg.write(valor.toString() + ";");
//            }
//            bwAlg.write("\n");

        } catch (FileNotFoundException ex) {
            System.out.println("Não foi possível encontrar o arquivo " + arqResult.getName());

        } catch (IOException ex) {
            Logger.getLogger(AnnStd.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            bwAlg.flush();
            bwAlg.close();

        }
    }

    //Busca se a lista de algoritmos passados já existe no arquivo responsável por armazenar listas de algoritmos anteriormente gerados
    private static boolean buscaAlgoritmos(File busca, ArrayList<Integer> elemento) throws IOException, InterruptedException {

        if (!busca.exists()) {

            busca.createNewFile();
            new Thread().sleep(50);

        }

//O gabarito tem de estar sem aspas
        String Str;
        boolean existe = false;

        BufferedReader brGS = null;
        try {
            brGS = new BufferedReader(new FileReader(busca.getPath()));

            while ((Str = brGS.readLine()) != null) {

                if ((elemento.toString().equals(Str))) {
                    existe = true;
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Não foi possível encontrar o arquivo " + busca.getName() + " em buscaGabarito()");

        } catch (IOException ex) {
            Logger.getLogger(AnnStd.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            brGS.close();
        }

        return existe;
    }

}
