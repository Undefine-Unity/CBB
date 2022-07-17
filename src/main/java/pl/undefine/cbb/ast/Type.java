package pl.undefine.cbb.ast;

import pl.undefine.cbb.Token;
import pl.undefine.cbb.TokenType;

public enum Type
{
    Int8("int8", "int8_t"),
    UInt8("uint8", "uint8_t"),
    Int16("int16", "int16_t"),
    UInt16("uint16", "uint16_t"),
    Int32("int32", "int32_t"),
    UInt32("uint32", "uint32_t"),
    Int64("int64", "int64_t"),
    UInt64("uint64", "uint64_t"),
    Float("float", "float"),
    Double("double", "double"),
    String("string", "char*"),
    Void("void", "void");

    public final String cbb_name;
    public final String cpp_name;

    Type(String cbb_name, String cpp_name)
    {
        this.cbb_name = cbb_name;
        this.cpp_name = cpp_name;
    }

    public static boolean is_type(Token token)
    {
        assert token.type == TokenType.Name;

        for(Type type : values())
        {
           if(token.value.equals(type.cbb_name))
           {
               return true;
           }
        }

        return false;
    }

    public static Type get_type(Token token)
    {
        assert token.type == TokenType.Name;

        for(Type type : values())
        {
            if(token.value.equals(type.cbb_name))
            {
                return type;
            }
        }

        return null;
    }

    public static Type get_type(String token_value)
    {
        for(Type type : values())
        {
            if(token_value.equals(type.cbb_name))
            {
                return type;
            }
        }

        return null;
    }
}
