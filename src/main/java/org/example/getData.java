package org.example;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class getData {
    //Diccionario de Categoria - Palabras no repetidas por categoria
    private HashMap<String, ArrayList> DataCategory = new HashMap<String,ArrayList>();
    //Diccionario de cantidad de oraciones por categoria
    private HashMap<String, Double> counterSentence = new HashMap<String,Double>();
    //Diccionario de probabilidad de que una oracion sea de una categoria
    private HashMap<String, Double> probabilityCategory = new HashMap<String,Double>();
    //Diccionario de total de palabras que existe en cada categoria
    private HashMap<String, Double> totalWordsCategory = new HashMap<String,Double>();
    //Arreglo con todas las palabras sin repetir
    private ArrayList<String> Diccionario  = new ArrayList<String>();
    //Diccionario de recurrencia de palabra por categoria
    private HashMap<String, HashMap<String, Double>> concurrencyWordCategory = new HashMap<String,HashMap<String, Double>>();

    private FileReader archCSV = null;
    private  CSVReader csvReader = null;
    //Contador de oraciones
    private double size ;

    private void addconcurrencyWordCategory(String category,String[] tokens){
        if(!concurrencyWordCategory.containsKey(category)){
            concurrencyWordCategory.put(category,new HashMap<String, Double>());
        }
        HashMap<String, Double> word = concurrencyWordCategory.get(category);
        for (int i = 0; i < tokens.length; i++) {
            if(!word.containsKey(tokens[i].toLowerCase()) && tokens[i].length() > 0){
                word.put(tokens[i].toLowerCase(),1.0);
            }
            else{
                if(tokens[i].length() > 0){
                    double counter = word.get(tokens[i].toLowerCase());
                    word.put(tokens[i].toLowerCase(),1.0+counter);
                }

            }
        }
    }

    private void addTotalWordsCategory(String category,Double count){
        if(!totalWordsCategory.containsKey(category)){
            totalWordsCategory.put(category,count);
        }
        else{
            double counter = totalWordsCategory.get(category);
            totalWordsCategory.put(category,count + counter);
        }
    }

    private void getprobabilityCategory(){
        for(Map.Entry<String, Double> entry : counterSentence.entrySet()) {
            probabilityCategory.put(entry.getKey(),entry.getValue()/size);
        }
    }

    private void countCategory(String category){
        if(!counterSentence.containsKey(category)){
            counterSentence.put(category,1.0);
        }
        else{
            double count = counterSentence.get(category);
            counterSentence.put(category,1.0 + count);
        }
    }

    private void tokenList(String[] tokens){
        //Ingresar datos al diccionario de palabras global
        for (int i = 0; i < tokens.length; i++) {
            if(!Diccionario.contains(tokens[i].toLowerCase())&& tokens[i].length() > 0){
                Diccionario.add(tokens[i].toLowerCase());
            }
        }
    }

    private void tokenCategory(String[] tokens,String category){
        //Al momento del ingreso de una llave que no se contenga en el diccionario de categorias
        if(!DataCategory.containsKey(category)){
            ArrayList<String> token = new ArrayList<>();
            for (int i = 0; i < tokens.length; i++) {
                if(!token.contains(tokens[i].toLowerCase()) && tokens[i].length() > 0){
                    token.add(tokens[i].toLowerCase());
                }
            }
            DataCategory.put(category, token);
        }
        //Momento en el que ya este ingresado en el diccionario de Categorias
        else {
            ArrayList<String> token = DataCategory.get(category);
            for (int i = 0; i < tokens.length; i++) {
                if(!token.contains(tokens[i].toLowerCase()) && tokens[i].length() > 0){
                    token.add(tokens[i].toLowerCase());
                }
            }
        }
    }

    public void readCsv(String path){
        try {
            size = 0;
            archCSV = new FileReader("data/test.csv");
            CSVParser conPuntoYComa = new CSVParserBuilder().withSeparator('|').build();
            csvReader = new CSVReaderBuilder(archCSV).withCSVParser(conPuntoYComa).build();
            String[] fila = null;
            while((fila = csvReader.readNext()) != null) {
                //Tokenizacion
                String datos = fila[0];
                String letras = "";
                for (int i=0; i< datos.length(); i++)
                {
                    if(Character.isLetter(datos.charAt(i))||datos.charAt(i) == ' '){
                        letras += datos.charAt(i);
                    }
                }
                //Contador de oraciones recorridas
                size++;
                //Contar oraciones por categoria
                this.countCategory(fila[1]);
                String[] tokens = letras.split(" ");
                //Agregar palabras por categoria
                this.tokenCategory(tokens,fila[1]);
                //Palabras totales
                this.tokenList(tokens);
                //Agregando cantidad de palabras por categoria
                this.addTotalWordsCategory(fila[1],Double.valueOf(tokens.length));
                //Agregando total de ocurrencia de palabras por categoria
                this.addconcurrencyWordCategory(fila[1],tokens);
            }
            this.getprobabilityCategory();
            csvReader.close();

        }
        catch(Exception e) {
            System.out.println("Error al leer csv");
        }
    }
}
