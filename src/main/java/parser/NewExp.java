package parser;

import java.util.Map;

public record NewExp(String structname, Map<String, Exp> fields) implements Exp{}
