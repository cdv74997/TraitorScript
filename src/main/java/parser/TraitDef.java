package parser;

import java.util.List;

public record TraitDef(String name, List<AbsMethodDef> methods) implements ProgramItem {}
