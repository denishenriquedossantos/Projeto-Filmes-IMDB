import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseMovieYear extends CellProcessorAdaptor {
    public ParseMovieYear() {
        super();
    }

    public ParseMovieYear(CellProcessorAdaptor next) {
        super(next);
    }

    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);

        final Integer result;
        if (value instanceof Integer) {
            result = (Integer) value;
        } else if (value instanceof String) {
            if (value.equals("PG")) {
                result = 1995;
            } else {
                try {
                    result = Integer.valueOf((String) value);
                } catch (final NumberFormatException e) {
                    throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as an Integer", value), context, this, e);
                }
            }
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format("the input value should be of type Integer or String but is of type %s", actualClassName), context, this);
        }

        return next.execute(result, context);
    }
}
