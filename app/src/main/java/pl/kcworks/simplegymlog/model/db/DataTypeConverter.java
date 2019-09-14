package pl.kcworks.simplegymlog.model.db;

import androidx.room.TypeConverter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import pl.kcworks.simplegymlog.model.ExerciseWithSets;

public class DataTypeConverter {

    @TypeConverter
    public static List<ExerciseWithSets> fromStringToExerciseWithSetsList(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ExerciseWithSets.class);
        JsonAdapter<List<ExerciseWithSets>> jsonAdapter = moshi.adapter(type);
        List<ExerciseWithSets> exerciseWithSetsList = null;
        try {
            exerciseWithSetsList = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  exerciseWithSetsList;
    }

    @TypeConverter
    public static String fromExerciseWithSetsListToString (List<ExerciseWithSets> exerciseWithSetsList) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ExerciseWithSets.class);
        JsonAdapter<List<ExerciseWithSets>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(exerciseWithSetsList);
    }

    public static String exerciseWithSetsToString(ExerciseWithSets exerciseWithSets) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ExerciseWithSets> jsonAdapter = moshi.adapter(ExerciseWithSets.class);

        return jsonAdapter.toJson(exerciseWithSets);
    }

    public static ExerciseWithSets stringToExerciseWithSets(String json) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ExerciseWithSets> jsonAdapter = moshi.adapter(ExerciseWithSets.class);

        try {
            return jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

// DataTypeConverter