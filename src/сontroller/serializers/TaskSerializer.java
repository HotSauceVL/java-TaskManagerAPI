package сontroller.serializers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import data.Status;
import data.Task;
import data.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TaskSerializer implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy: HH.mm");

        @Override
        public Task deserialize(JsonElement jsonElement, Type type,
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

            if (startTime == null) {
                if (id == 0)
                    return new Task(title, description, status);
                else
                    return new Task(id, title, description, status);
            } else
                if (id == 0)
                    return  new Task(title, description, status, startTime, duration);
                else
                    return new Task(id, title, description, status, startTime, duration);
        }

        @Override
        public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result = new JsonObject();
            result.addProperty("id", task.getId());
            result.addProperty("title", task.getTitle());
            result.addProperty("description", task.getDescription());
            result.addProperty("status", task.getStatus().toString());
            if (task.getStartTime().isPresent()) {
                result.addProperty("startTime", task.getStartTime().get().format(formatter));
                result.addProperty("endTime", task.getEndTime().get().format(formatter));
            }
            return result;
        }
}
