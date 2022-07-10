package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class ParsedFile
{
    public int file_id;
    public List<Function> functions;

    public ParsedFile(int file_id)
    {
        this.file_id = file_id;
        this.functions = new ArrayList<>();
    }
}
