package net.trincom.websiteserver.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// https://stackoverflow.com/questions/33512037/a-regular-expression-generator-for-number-ranges
public class RangeRegexGenerator
{

    /**
     * Generates regex from a range
     * @param min
     * @param max
     * @return
     */
    public String range(int min, int max) {
        String regex = "(";
        List<String> regexParts = getRegex(min + "", max + "");
        for (String part : regexParts) {
            regex = regex + part + "|";
        }
        regex = removeLastChar(regex) + ")";
        return regex;
    }

    /**
     * Removes last character from string
     * @param str
     * @return
     */
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    /**
     * Return a list of regular expressions that match the numbers
     * that fall within the range of the given numbers, inclusive.
     * Assumes the given strings are numbers of the the same length,
     * and 0-left-pads the resulting expressions, if necessary, to the
     * same length.
     * @param begStr
     * @param endStr
     * @return
     */
    private List<String> getRegex(String begStr, String endStr)
    {
        int start = Integer.parseInt(begStr);
        int end   = Integer.parseInt(endStr);
        int stringLength = begStr.length();
        List<Integer> pairs = getRegexPairs(start, end);
        List<String> regexes = toRegex(pairs, stringLength);
        return regexes;
    }

    /**
     * return the list of integers that are the paired integers
     * used to generate the regular expressions for the given
     * range. Each pair of integers in the list -- 0,1, then 2,3,
     * etc., represents a range for which a single regular expression
     * is generated.
     * @param start
     * @param end
     * @return
     */
    private static List<Integer> getRegexPairs(int start, int end)
    {
        List<Integer> pairs = new ArrayList<>();
        if (start > end) return pairs; // empty range
        int firstEndingWith0 = 10*((start+9)/10); // first number ending with 0
        if (firstEndingWith0 > end) // not in range?
        {
            // start and end differ only at last digit
            pairs.add(start);
            pairs.add(end);
            return pairs;
        }

        if (start < firstEndingWith0) // start is not ending in 0
        {
            pairs.add(start);
            pairs.add(firstEndingWith0-1);
        }

        int lastEndingWith9 = 10*(end/10)-1; // last number in range ending with 9
        // all regex for the range [firstEndingWith0,lastEndingWith9] end with [0-9]
        List<Integer> pairsMiddle = getRegexPairs(firstEndingWith0/10, lastEndingWith9/10);
        for (int i=0; i<pairsMiddle.size(); i+=2)
        {
            // blow up each pair by adding all possibilities for appended digit
            pairs.add(pairsMiddle.get(i)  *10+0);
            pairs.add(pairsMiddle.get(i+1)*10+9);
        }

        if (lastEndingWith9 < end) // end is not ending in 9
        {
            pairs.add(lastEndingWith9+1);
            pairs.add(end);
        }

        return pairs;
    }

    /**
     * return the regular expressions that match the ranges in the given
     * list of integers. The list is in the form firstRangeStart, firstRangeEnd,
     * secondRangeStart, secondRangeEnd, etc. Each regular expression is 0-left-padded,
     * if necessary, to match strings of the given width.
     * @param pairs
     * @param minWidth
     * @return
     */
    private List<String> toRegex(List<Integer> pairs, int minWidth)
    {
        List<String> list = new ArrayList<>();
        String numberWithWidth = String.format("%%0%dd", minWidth);
        for (Iterator<Integer> iterator = pairs.iterator(); iterator.hasNext();)
        {
            String start = String.format(numberWithWidth, iterator.next());
            String end = String.format(numberWithWidth, iterator.next());

            list.add(toRegex(start, end));
        }
        return list;
    }

    /**
     * return a regular expression string that matches the range
     * with the given start and end strings.
     * @param start
     * @param end
     * @return
     */
    private String toRegex(String start, String end)
    {
        assert start.length() == end.length();

        StringBuilder result = new StringBuilder();

        for (int pos = 0; pos < start.length(); pos++)
        {
            if (start.charAt(pos) == end.charAt(pos))
            {
                result.append(start.charAt(pos));
            } else
            {
                result.append('[').append(start.charAt(pos)).append('-')
                        .append(end.charAt(pos)).append(']');
            }
        }
        return result.toString();
    }
}