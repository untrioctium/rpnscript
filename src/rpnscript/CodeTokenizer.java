/*
 * CodeTokenizer.java
 *
 * Copyright (c) 2009 Alex Riley <a.s.riley at gmail.com>. All rights reserved.
 *
 * This file is part of RPNScript.
 *
 * RPNScript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPNScript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPNScript.  If not, see <http://www.gnu.org/licenses/>.
 */

package rpnscript;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

/**
 * Internal class that helps process raw RPNScript code.  Code is split up by
 * tokens separated by any amount of whitespace or parenthesis.
 * @author Alex
 */
public class CodeTokenizer
{

    /**
     * Processes RPNScript code into tokens.
     * @param code The code to process.
     */
    public CodeTokenizer( String code )
    {
        String[] codeLines = code.split( "\\r?\\n" );

        codeTokens = new ArrayList();

        for ( String line : codeLines )
        {
            line = line.trim();

            List<String> lineTokens = new ArrayList();

            boolean inQuotes = false;

            String token = new String();

            while ( true )
            {
                if ( !inQuotes )
                {
                    line = line.trim();
                    if ( line.length() == 0 )
                    {
                        break;
                    }

                    int quotePos = line.indexOf( '"' );

                    if ( quotePos == -1 )
                    {
                        line = line.split( "//" )[0];

                        lineTokens.addAll( Arrays.asList( line.replace( "(", " " ).replace( ")", " " ).split( "\\s+" ) ));
                        break;
                    }
                    else
                    {
                        if ( line.substring( 0, quotePos ).length() > 0 )
                        {
                            String[] subLine = line.substring( 0, quotePos ).split( "//" );
                            lineTokens.addAll( Arrays.asList( subLine[0].replace( "(", " " ).replace( ")", " " ).split( "\\s+" ) ) );

                            if( subLine.length > 1 )
                                continue;
                        }
                        line = line.substring( quotePos + 1 );
                        inQuotes = true;
                    }
                }
                else
                {
                    int lastPos = 0;
                    int quotePos = 0;
                    while ( true )
                    {
                        quotePos = line.indexOf( '"', lastPos );

                        if ( quotePos == -1 )
                        {
                            break;
                        }

                        boolean escapedQuote = false;
                        for ( int i = quotePos - 1; i >= 0 && line.charAt( i ) == '\\'; i-- )
                        {
                            escapedQuote = !escapedQuote;
                        }

                        if ( escapedQuote )
                        {
                            lastPos = quotePos + 1;
                        }
                        else
                        {

                            lineTokens.add( "\"" + line.substring( 0, quotePos ).replace( "\\\"", "\"" ).replace( "\\n", "\n").replace("\\t", "\t") + "\"" );
                            line = line.substring( quotePos + 1 );
                            inQuotes = false;
                            break;
                        }
                    }
                }
            }

            if( lineTokens.size() > 0)
            {
                while( lineTokens.remove( "" ));
                codeTokens.add( lineTokens );
            }
  
        }

        currentLine = 0;
        currentToken = -1;
    }

    /**
     * Indicates if the tokenizer has more tokens.
     * @return Whether the tokenizer has more tokens.
     */
    public boolean hasToken()
    {
        if ( currentToken + 1 == codeTokens.get( currentLine ).size() &&
                currentLine + 1 == codeTokens.size() )
        {
            return false;
        }

        return true;
    }

    /**
     * Gets the next token in the tokenizer.
     * @return The next token.
     */
    public String getToken()
    {
        currentToken++;

        if ( currentToken == codeTokens.get( currentLine ).size() )
        {
            currentLine++;
            currentToken = 0;

            if ( currentLine == codeTokens.size() )
            {
                return null;
            }
        }
        return codeTokens.get( currentLine ).get( currentToken );
    }

    /**
     * Gets the line that the current token is on.
     * @return The line number.
     */
    public int getCurrentLine()
    {
        return currentLine + 1;
    }
    private int currentLine;
    private int currentToken;
    private List<List<String>> codeTokens;
}
