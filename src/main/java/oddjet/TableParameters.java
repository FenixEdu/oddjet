package oddjet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableParameters {

    public static enum FillBehavior {
        APPEND, // Append data to last paragraph
        SKIP,   // If any paragraph has content keep the data for the next cell
        STEP,   // If any paragraph has content throw away the data
        ADD;    // Add data to cell in new paragraph 
    }

    public static enum FillDirection {
        VERTICAL,   // Data categories are columns
        HORIZONTAL; // Data categories are rows    (in positional fill, flip the data)
    }

    public static enum FillType {
        CATEGORICAL,    // Search for categories to be used in the table
        POSITIONAL      // Use the data as a whole disregarding categories
    }

    public static enum Parameter {
        PARAMETER("(\\w+)"),                                                        // Generic Parameter
        HEADER("^(nhr|noheader)|(?:hdr|header)(\\d+)_(\\d+)$"),                     // Table Header Parameter, TableCoordenate of the first non-header cell.
        FILL_TYPE("^(pos|positional)|(cat|categorical)$"),                          // FillType Parameter, see above.
        FILL_BEHAVIOR("^(skp|skip)|(stp|step)|(apd|append)|(add|addparagraph)$"),   // FillBehavior Parameter, see above.
        FILL_DIRECTION("^(fiv|fivert|fillvertical)|(fih|fihorz|fillhorizontal)$"),  // FillDirection Parameter, see above.
        STYLE_SOURCE(                                                               // Style Source Parameter, relative TableCoordenate from which to copy the style 
                "^(pre|prest|prestyled)|"                                           // don't copy any style
                        + "(vst|vertst|verticalstyle)|"                             // copy style from top cell
                        + "(hst|horzst|horizontalstyle)|"                           // copy style from left cell
                        + "((?:pst|perdst|periodicstyle)(\\d+)_(\\d+))$"),          // copy style from cell at specific distance
        TOGGLE_LAST_BORDER("^(tlb|togglelb|togglelastborder)$");                    // Toggle Last Border Parameter, toggle bottom border of last row/column
        // TODO allow specifying border style: lineType,color,width,inner width,outer width
        // TODO implement complete border parameters and then ditch the TOGGLE_LAST_BORDER parameter (maybe replaced with border=bottom_...)

        private Pattern pattern;

        private Parameter(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public Pattern getPattern() {
            return pattern;
        }

        public Matcher getMatcher(String param) {
            return pattern.matcher(param);
        }

        public static boolean readInto(String param, TableParameters tp) {
            param = param.toLowerCase();
            Matcher matcher = null;
            if ((matcher = Parameter.HEADER.getMatcher(param)).find()) {
                tp.setHeaders(matcher.group(1) != null ? new TableCoordenate() : new TableCoordenate(Integer.parseInt(matcher
                        .group(2)), Integer.parseInt(matcher.group(3))));
            } else if ((matcher = Parameter.FILL_TYPE.getMatcher(param)).find()) {
                tp.setFillType(matcher.group(1) != null ? FillType.POSITIONAL : FillType.CATEGORICAL);
            } else if ((matcher = Parameter.FILL_BEHAVIOR.getMatcher(param)).find()) {
                tp.setFillBehavior(matcher.group(1) != null ? FillBehavior.SKIP : matcher.group(2) != null ? FillBehavior.STEP : matcher
                        .group(3) != null ? FillBehavior.APPEND : FillBehavior.ADD);
            } else if ((matcher = Parameter.FILL_DIRECTION.getMatcher(param)).find()) {
                tp.setFillDirection(matcher.group(1) != null ? FillDirection.VERTICAL : FillDirection.HORIZONTAL);
            } else if ((matcher = Parameter.STYLE_SOURCE.getMatcher(param)).find()) {
                if (matcher.group(4) != null && matcher.group(5) != null) {
                    int col = Integer.parseInt(matcher.group(4));
                    int row = Integer.parseInt(matcher.group(5));
                    if (row == 0 && col == 0) {
                        // Style source (0,0) is the same as prestyled
                        tp.setStyleRCoord(null);
                    } else {
                        tp.setStyleRCoord(new TableCoordenate(col, row));
                    }
                } else {
                    tp.setStyleRCoord(matcher.group(2) != null ? new TableCoordenate(0, 1) : matcher.group(3) != null ? new TableCoordenate(
                            1, 0) : null);
                }
            } else if ((matcher = Parameter.TOGGLE_LAST_BORDER.getMatcher(param)).find()) {
                tp.setToggleLastBorder(true);
            } else {
                return false;
            }
            return true;
        }

    }

    private FillType fillType = FillType.CATEGORICAL;
    private FillBehavior fillBehavior = FillBehavior.APPEND;
    private FillDirection fillDirection = FillDirection.VERTICAL;
    private boolean toggleLastBorder = false;
    private TableCoordenate styleRCoord = new TableCoordenate(0, 1);
    private TableCoordenate headers = new TableCoordenate(0, 1);

    public TableCoordenate getHeaders() {
        return headers;
    }

    public void setHeaders(TableCoordenate headers) {
        this.headers = headers;
    }

    public FillType getFillType() {
        return fillType;
    }

    public void setFillType(FillType fillType) {
        this.fillType = fillType;
    }

    public FillBehavior getFillBehavior() {
        return fillBehavior;
    }

    public void setFillBehavior(FillBehavior fillBehavior) {
        this.fillBehavior = fillBehavior;
    }

    public FillDirection getFillDirection() {
        return fillDirection;
    }

    public void setFillDirection(FillDirection fillDirection) {
        this.fillDirection = fillDirection;
    }

    public TableCoordenate getStyleRCoord() {
        return styleRCoord;
    }

    public void setStyleRCoord(TableCoordenate formatRCoord) {
        this.styleRCoord = formatRCoord;
    }

    public boolean shouldToggleLastBorder() {
        return toggleLastBorder;
    }

    public void setToggleLastBorder(boolean toggleLastBorder) {
        this.toggleLastBorder = toggleLastBorder;
    }
}
