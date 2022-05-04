package сontroller.serializers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import data.Epic;
import data.Status;
import data.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EpicSerializer implements JsonSerializer<Epic>, JsonDeserializer<Epic> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy: HH.mm");

    @Override
    public Epic deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        long id = 0;
        if (jsonObject.has("id")) {
            id = jsonObject.get("id").getAsLong();
        }
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString().toUpperCase());

        if (id == 0)
            return  new Epic(title, description, status);
        else
            return new Epic(id, title, description, status);
    }

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", epic.getId());
        result.addProperty("title", epic.getTitle());
        result.addProperty("description", epic.getDescription());
        result.addProperty("status", epic.getStatus().toString());
        if (epic.getStartTime().isPresent()) {
            result.addProperty("startTime", epic.getStartTime().get().format(formatter));
            result.addProperty("endTime", epic.getEndTime().get().format(formatter));
        }
        result.addProperty("subTasks", epic.getSubTasks().toString());
        return result;
    }
}

