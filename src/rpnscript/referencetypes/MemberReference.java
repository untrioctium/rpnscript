/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.referencetypes;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import rpnscript.RPNProgram;
import rpnscript.StackValue;
import rpnscript.RPNScriptException;

/**
 * Class derived from StackValue that refers to a variable.
 * @author Alex
 */
public class MemberReference extends StackValue
{

    /**
     * Holds the name of the variable being referenced.
     */
    private String memberName;

    /**
     * Create a reference to a variable.
     * @param name
     */
    public MemberReference( String name )
    {
        memberName = name;

        if( numConversionTable == null)
        {
            numConversionTable = new HashMap();

            try
            {
                numConversionTable.put( Byte.TYPE, Number.class.getMethod( "byteValue" ) );
                numConversionTable.put( Double.TYPE, Number.class.getMethod( "doubleValue" ) );
                numConversionTable.put( Float.TYPE, Number.class.getMethod( "floatValue" ) );
                numConversionTable.put( Integer.TYPE, Number.class.getMethod( "intValue" ) );
                numConversionTable.put( Long.TYPE, Number.class.getMethod( "longValue" ) );
                numConversionTable.put( Short.TYPE, Number.class.getMethod( "shortValue" ) );
            }
            catch( Exception e )
            {

            }


        }
    }

    /**
     * Checks to see if the variable refers to a user function. If it does, execution
     * is branched to that function, but if it doesn't, a reference to the variable
     * is pushed on the stack.
     * @param p The program invoking the operation.
     * @throws rpnscript.RPNScriptException There was an error accessing the variable.
     */
    @Override
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object object = p.popObject();

        if ( !p.loadedClasses.containsValue( object.getClass() ) )
        {
            return;
        }

        for ( Method m : object.getClass().getMethods() )
        {
            if ( m.getName().equals( memberName ) )
            {
                Class[] argTypes = m.getParameterTypes();

                ArrayList arguments = new ArrayList();
                try
                {
                    for ( int i = argTypes.length - 1; i >= 0; i-- )
                    {
                        Object value = p.popObject();

                        if ( argTypes[i].isPrimitive() )
                        {
                            if( argTypes[i] == Boolean.TYPE)
                            {
                                arguments.add( 0, p.toBoolean( value ));
                            }
                            else 
                            {
                                arguments.add( 0, numConversionTable.get( argTypes[i]).invoke( value ));
                            }
                            
                        }
                        else
                        {
                            Object arg = argTypes[i].newInstance();
                            arguments.add( 0, arg );
                        }

                    }

                    Object result = null;


                    if ( argTypes.length == 0 )
                    {
                        result = m.invoke( object );
                    }
                    else
                    {
                        result = m.invoke( object, arguments.toArray() );
                    }

                    if ( result != null )
                    {
                        Class clazz = result.getClass();
                        if( clazz.isPrimitive() )
                        {
                            if( clazz == Float.TYPE || clazz == Double.TYPE )
                            {
                                p.pushObject( ((Number)result).doubleValue() );
                            }
                            else if( clazz == Boolean.TYPE )
                            {
                                p.pushObject( result );
                            }
                            else if( clazz == Character.TYPE )
                            {
                                p.pushObject( ((Character)result).toString());
                            }
                            else
                            {
                                p.pushObject( ((Number)result).longValue());
                            }
                        } else p.pushObject( result );
                        
                    }
                    p.executionStackPosition++;
                    return;
                } catch ( Exception e )
                {
                    System.out.println( e.toString() );
                    e.printStackTrace();
                    p.executionStackPosition++;
                    return;
                }
            }
        }

        p.pushObject( object );
        super.operate( p );
    }

    @Override
    public void setValue( RPNProgram p, Object o )
    {
        try
        {
            Object object = p.popObject();
            if ( !p.loadedClasses.containsValue( object.getClass() ) )
            {
                return;
            }
            Field f = object.getClass().getDeclaredField( memberName );

            f.set( object, o );
        } catch ( Exception e )
        {
        }

    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Object getValue( RPNProgram p )
    {
        try
        {
            Object object = p.popObject();
            if ( !p.loadedClasses.containsValue( object.getClass() ) )
            {
                return null;
            }
            Field f = object.getClass().getDeclaredField( memberName );
            return f.get( object );
        } catch ( Exception e )
        {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String getMemberName()
    {
        return memberName;
    }

    private static Map<Class,Method> numConversionTable = null;
}
