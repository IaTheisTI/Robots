package state;

import log.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для чтения и записи состояний окон
 */
public class State {

    private final String HOME_DIR = System.getProperty("user.home");
    private final String FILE_NAME = HOME_DIR + "\\save.txt";

    /**
     * чтение состояний окон из файла
     */
    public Map<String, String> readStateFromFile(){
        Map<String, String> result = new HashMap<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME)))
        {
            String data = bufferedReader.readLine();
            while(data != null) {
                String[] keyValuePair = data.split(" ");
                result.put(keyValuePair[0], keyValuePair[1]);
                data = bufferedReader.readLine();
            }
            return result;
        }
        catch(IOException ex){
            Logger.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Запись состояния окна в файл
     */
    public void writeStateInFile(Map<String, String> state){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME)))
        {
            for(Map.Entry<String, String> entry : state.entrySet()) {
                bufferedWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }
        catch(IOException ex){
            Logger.error(ex.getMessage());
        }
    }
}