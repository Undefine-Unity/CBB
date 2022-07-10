package pl.undefine.cbb;

public class Span
{
    public int file_id;
    public int start;
    public int end;

    public Span(int file_id, int start, int end)
    {
        this.file_id = file_id;
        this.start = start;
        this.end = end;
    }
}
