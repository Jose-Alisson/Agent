package sunshineax.agent.x.request;

import sunshineax.agent.annotations.Message;
import sunshineax.agent.validator.annotations.NotBlack;
import sunshineax.agent.validator.annotations.NotNull;
import sunshineax.agent.validator.annotations.Size;
import sunshineax.agent.validator.annotations.Validate;

@Message(intent = "command")
@Validate
public class Data {

    @NotNull
    @Size(min = 1)
    public String name;

    public String description;

    public String date;

    public Data() {
    }

    public Data(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
