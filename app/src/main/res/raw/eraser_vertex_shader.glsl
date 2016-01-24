attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
varying vec2 v_Position;


void main()                    
{                            
    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = a_Position;
    v_Position =  gl_Position.xy;
} 