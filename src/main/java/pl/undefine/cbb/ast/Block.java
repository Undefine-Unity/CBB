package pl.undefine.cbb.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Block is a structure that contains a list of statements,
 * usually contains everything between `{` and `}`
 *
 * @see Statement
 */
public class Block
{
    /**
     * Statements contained inside of the block
     */
    public List<Statement> statements;

    public Block()
    {
        statements = new ArrayList<>();
    }
}
