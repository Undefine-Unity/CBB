package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

public class ParsedFile
{
    public int file_id;
    public List<Declaration> declarations;

    public ParsedFile(int file_id)
    {
        this.file_id = file_id;
        this.declarations = new ArrayList<>();
    }
}
