package pl.kcworks.simplegymlog.model.db;

import androidx.room.TypeConverter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import pl.kcworks.simplegymlog.model.DayOfRoutine;

public class DataTypeConverter {

    @TypeConverter
    public static List<DayOfRoutine> fromStringToDayOfRoutineList(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, DayOfRoutine.class);
        JsonAdapter<List<DayOfRoutine>> jsonAdapter = moshi.adapter(type);
        List<DayOfRoutine> dayOfRoutineList = null;
        try {
            dayOfRoutineList = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  dayOfRoutineList;
    }

    @TypeConverter
    public static String fromDayOfRoutineListToString (List<DayOfRoutine> dayOfRoutineList) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, DayOfRoutine.class);
        JsonAdapter<List<DayOfRoutine>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(dayOfRoutineList);
    }
}

// DataTypeConverter