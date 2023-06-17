package edu.clientserver.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Group {

    public static final String TABLE = "good_group";
    public static final String ID = "id";
    public static final String MAME = "name";

    private Long id;
    private String name;
}
