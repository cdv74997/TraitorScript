package parser;

import java.util.List;

public record ImplDef(String traitName, Type forType, List<ConcMethodDef> methods) implements ProgramItem {}
