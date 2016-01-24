precision lowp float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main()
{
    vec4 mask = texture2D(u_TextureUnit, v_TextureCoordinates);
    float grey = dot(mask.rgb, vec3(0.299, 0.587, 0.114));
    gl_FragColor = vec4(mask.rgb, grey);
}