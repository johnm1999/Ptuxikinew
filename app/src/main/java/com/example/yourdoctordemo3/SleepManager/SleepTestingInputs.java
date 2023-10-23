package com.example.yourdoctordemo3.SleepManager;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.SleepStages;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.yourdoctordemo3.SleepManager.SleepRecordingHelper.createDataSet;

public class SleepTestingInputs {

    /**
     * Attention: The list of dataSets returned is hardcoded for the purposes of this project
     *
     * @return Method returning a List<DataSet>
     * @throws ParseException
     */
    protected static List<DataSet> createSleepDataSets() throws ParseException, JSONException {
        List<DataSet> dataSets = new ArrayList<>();

        dataSets.add(createDataSet("10-10-2021T12:00:00Z", getMap("Mon")));
        dataSets.add(createDataSet("11-10-2021T12:00:00Z", getMap("Tue")));
        dataSets.add(createDataSet("12-10-2021T12:00:00Z", getMap("Wed")));
        dataSets.add(createDataSet("13-10-2021T12:00:00Z", getMap("Thu")));
        dataSets.add(createDataSet("14-10-2021T12:00:00Z", getMap("Fri")));
        dataSets.add(createDataSet("15-10-2021T12:00:00Z", getMap("Sat")));
        dataSets.add(createDataSet("16-10-2021T12:00:00Z", getMap("Sun")));

        return dataSets;
    }


    /**
     *
     * @param day Day for which will create the testing data
     *
     * @return A JSONObject which has as key the type of sleep and as value the duration of it
     *
     * @throws JSONException JSON related problems coming from the JSON API such as: Parsing Errors etc...
     */
    protected static JSONObject getMap(String day) throws JSONException {
        JSONObject jo = new JSONObject();

        switch (day) {
            case "Mon":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                break;
            case "Tue":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 30L);
                jo.put(String.valueOf(SleepStages.AWAKE), 30L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                break;
            case "Wed":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 120L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                break;
            case "Thu":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.AWAKE), 30L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 120L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                break;
            case "Fri":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 30L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 90L);
                break;
            case "Sat":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 120L);
                break;
            case "Sun":
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_LIGHT), 30L);
                jo.put(String.valueOf(SleepStages.AWAKE), 30L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_REM), 60L);
                jo.put(String.valueOf(SleepStages.SLEEP_DEEP), 120L);
                break;
        }
        return jo;
    }
}
