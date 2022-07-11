package pl.undefine.cbb.utils;

public class ErrorOr<T>
{
    Error error;
    T value;

    public ErrorOr(Error error)
    {
        this.error = error;
        this.value = null;
    }

    public ErrorOr(T value)
    {
        this.error = null;
        this.value = value;
    }

    public boolean is_error()
    {
        return error != null;
    }

    public T get_value()
    {
        if(is_error())
        {
            assert false;
            System.out.println("internal error");
            System.exit(2);
        }
        return value;
    }

    public Error get_error()
    {
        if(!is_error())
        {
            assert false;
            System.out.println("internal error");
            System.exit(2);
        }
        return error;
    }

    public <T> ErrorOr<T> rethrow()
    {
        if(!is_error())
        {
            assert false;
            System.out.println("internal error");
            System.exit(2);
        }
        return new ErrorOr<>(error);
    }
}
