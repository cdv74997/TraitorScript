package parser;

import java.util.List;

public record Program(List<ProgramItem> items, List<Stmt> entryPoint) {}