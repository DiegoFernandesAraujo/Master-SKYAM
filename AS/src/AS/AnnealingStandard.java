/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AS;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Diego
 */
public class AnnealingStandard {

    public static void main(String[] args) throws IOException {
        ManipulaCSV obj = new ManipulaCSV();
//        File arquivo = new File("D:\\Pesquisa\\Desenvolvimento\\resultado1.csv");
//        File arquivo = new File("H:\\Meu Drive\\UFCG\\Pesquisa\\Desenvolvimento\\resultado1.csv");

//        File arquivo = new File("./src/csv/","resultado1.csv");
//        File resultado1 = new File("./src/csv/","testeResult1.csv");
        File resultado1 = new File("./src/csv/", "resultTestCD1.csv");
        File resultado2 = new File("./src/csv/", "resultTestCD2.csv");
        File gs = new File("./src/csv/", "cd_gold.csv");

        /* Para retornar o path do projeto
        try {

            System.out.println(".. -> " + new File("..").getCanonicalPath());
            System.out.println(".  -> " + new File(".").getCanonicalPath());
            System.out.println(System.getProperty("user.dir"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
         */
//        obj.readCsvFile();
        obj.setGs(gs);
//        obj.comparaConjuntos(obj.padronizaCsvFile(resultado1));
        obj.comparaConjuntos(obj.padronizaCsvFile(resultado2));
//        obj.comparaComGS(obj.padronizaCsvFile(resultado1));
//        File juncaoTeste = obj.juntaArquivos(obj.padronizaCsvFile(resultado1), obj.padronizaCsvFile(resultado2));
//        obj.comparaComGS(obj.padronizaCsvFile(resultado));
//           obj.padronizaCsvFile(resultado);
//        obj.atualizaD_A(juncaoTeste);
        
//        System.out.println(juncaoTeste.getPath());

        System.out.println("Iteração " + obj.getIteracao());

    }

}
