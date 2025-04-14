package parser;

public record BinOpExp(Exp left, Op op, Exp right) implements Exp {}
