package ee.dm;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static ee.dm.util.Constant.*;


public class MainDetector {
    private static Logger logger = Logger.getLogger(MainDetector.class.getName());

    private static Properties languageResourceStream;

    private static String[] triGrams = {
            EN_TRIGRAM,DE_TRIGRAM,FR_TRIGRAM,
            ES_TRIGRAM,DA_TRIGRAM,FI_TRIGRAM,
            SV_TRIGRAM,RU_TRIGRAM,PL_TRIGRAM,
            IS_TRIGRAM,
    };

    private static String[] diGrams = {
            EN_DIGRAM,DE_DIGRAM,FR_DIGRAM,
            ES_DIGRAM,DA_DIGRAM,FI_DIGRAM,
            SV_DIGRAM,RU_DIGRAM,PL_DIGRAM,
            IS_DIGRAM,
    };

    private static String[] oneGrams = {
            EN_ONEGRAM,DE_ONEGRAM,FR_ONEGRAM,
            ES_ONEGRAM,DA_ONEGRAM,FI_ONEGRAM,
            SV_ONEGRAM,RU_ONEGRAM,PL_ONEGRAM,
            IS_ONEGRAM,
    };

    public static void main(String[] args) throws Exception{
        computeFor(triGrams);
        computeFor(diGrams);
        computeFor(oneGrams);
    }

    public static void computeFor(String[] possibleGram) throws Exception{
        InputStream sampleTextStream = MainDetector.class.getResourceAsStream("/test.txt");
        String input = IOUtils.toString(sampleTextStream, "UTF-8");
        languageResourceStream = MainDetector.readPropertyFile();

        Map<String, Double> gramCount = MainDetector.countOfgram(input, possibleGram);
        Map<String, Double> sumforEach =  MainDetector.sumOfgram(gramCount, possibleGram);
        Map<String, Double> percentages = MainDetector.getPercentage(sumforEach, getTotal(sumforEach));
        printOutput(percentages);
    }


    public static Properties readPropertyFile() {
        Properties prop = new Properties();
        try {
            prop.load(MainDetector.class.getClassLoader().getResourceAsStream("language.properties"));

        } catch (IOException io) {
            logger.severe(io.getMessage());
        }
        return prop;
    }

    public static List<String> getKeysFor(String gramPrefix){
        List<String> gramElements = Lists.newArrayList();

        for(Object obj: languageResourceStream.keySet()){
            if(((String) obj).startsWith(gramPrefix)){
                gramElements.add((String)obj);
            }
        }

        return gramElements;
    }



    private static Map countOfgram(String text, String[] typeOfGram){
        Map<String, Double> gramWithCount = new HashMap<String, Double>();

         for (int i = 0; i < typeOfGram.length; i++) {

            for (String typeOfGramKey : getKeysFor(typeOfGram[i])) {
                String textToSearch = typeOfGramKey.substring(typeOfGramKey.lastIndexOf(".")+1);
                if (text.toUpperCase().contains(textToSearch)) {
                     gramWithCount.put(typeOfGramKey,
                             StringUtils.countMatches(text.toUpperCase(), textToSearch) * Double.parseDouble((String)languageResourceStream.get(typeOfGramKey)));
                }
            }
        }
        return gramWithCount;
    }

    public static Map<String, Double> sumOfgram(Map<String,Double> gramWithCount, String[] typeOfGram){
        Map<String, Double> sum = new HashMap<String, Double>();
        for (String aTypeOfGram : typeOfGram) {
            sum.put(aTypeOfGram, sumForKey(aTypeOfGram, gramWithCount));
        }
        return sum;
    }

    public static double sumForKey(String gramPrefix, Map gramWithCount){
        double total = 0;

        for(Object obj: gramWithCount.keySet()){
            String key = (String) obj;
            if((key).startsWith(gramPrefix)){
                total +=  (Double) gramWithCount.get(key);
            }
        }

        return total;
    }

    private static double getTotal(Map<String, Double> sumOfEach){
        double sum = 0;
        for (String obj : sumOfEach.keySet()) {
            sum += sumOfEach.get(obj);
        }
        return sum;
    }

    public static Map<String, Double>  getPercentage(Map<String, Double> gramWithCount,double sum){
        Map<String, Double> percentages = new HashMap<String, Double>();
        for (String s: gramWithCount.keySet()){
            percentages.put(s,(gramWithCount.get(s)/sum) * 100);
        }
        return percentages;
    }

    public static void printOutput(Map<String, Double> percentagesOutput){
        System.out.println("*********************The output *****************************");
        for (String s: percentagesOutput.keySet()){
            System.out.println("The percentage of " + s + " is " + percentagesOutput.get(s));
        }
        System.out.println("*********************The End *****************************\n");
    }
}
