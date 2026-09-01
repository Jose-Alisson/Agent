package sunshineax.agent.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.data.session.Session;

import java.util.*;
import java.util.stream.Collectors;

public interface Room {
    String name();
    void add(String session);
    void remove(String session);
    boolean contains(String session);
    List<String> children();
}
