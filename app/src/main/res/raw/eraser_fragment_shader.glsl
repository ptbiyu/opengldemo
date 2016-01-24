precision mediump float; 

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;
varying vec2 v_Position;
uniform sampler2D srcTexture;

void main()
{
     vec3 mask = texture2D(u_TextureUnit, v_TextureCoordinates).rgb;
     float grey = dot(mask, vec3(0.299, 0.587, 0.114));
     vec3 src = texture2D(srcTexture, v_Position * 0.5 + 0.5).rgb;
     float alpha = clamp(grey, 0.0, 1.0);
     gl_FragColor = vec4(src, alpha);
}