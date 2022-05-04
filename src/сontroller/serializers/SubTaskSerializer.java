package сontroller.serializers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import data.Status;
import data.SubTask;
import data.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTaskSerializer implements JsonSerializer<SubTask>, JsonDeserializer<SubTask> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy: HH.mm");

    @Override
    public SubTask deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        long id = 0;
        LocalDateTime startTime = null;
        Duration duration = Duration.ZERO;
        if (jsonObject.has("id")) {
            id = jsonObject.get("id").getAsLong();
        }
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString().toUpperCase());
        if (jsonObject.has("startTime")) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
            duration = Duration.between(startTime,
                    LocalDateTime.parse(jsonObject.get("endTime").getAsString(), formatter));
        }
        long epicID = jsonObject.get("epicID").getAsLong();

        if (startTime == null) {
            if (id == 0)
                return new SubTask(title, description, status, epicID);
            else
                return new SubTask(id, title, description, status, epicID);
        } else
        if (id == 0)
            return  new SubTask(title, description, status, startTime, duration, epicID);
        else
            return new SubTask(id, title, description, status, startTime, duration, epicID);
    }

    @Override
    public JsonElement serialize(SubTask subTask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", subTask.getId());
        result.addProperty("title", subTask.getTitle());
        result.addProperty("description", subTask.getDescription());
        result.addProperty("status", subTask.getStatus().toString());
        if (subTask.getStartTime().isPresent()) {
            result.addProperty("startTime", subTask.getStartTime().get().format(formatter));
            result.addProperty("endTime", subTask.getEndTime().get().format(formatter));
        }
        result.addProperty("epicID", subTask.getEpicID());
        return result;
    }
}
