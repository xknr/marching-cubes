// copied skeleton glsl code from
//https://github.com/genekogan/Processing-Shader-Examples/blob/master/ColorShaders/data/sinewave.glsl

#define PROCESSING_COLOR_SHADER


void main( void ) {

	//vec2 position = gl_FragCoord.xy / resolution.xy;

	vec3 background = vec3(200/255.0);
	float depth = gl_FragCoord.z;
	vec3 diff = vec3(0.0);

	const float fogStart = 0.5;
	const float fogEnd = 1.0;
	float fogt = clamp((depth - fogStart) / (fogEnd - fogStart), 0.0, 1.0);

	vec3 col = (1.0 - fogt) * diff + fogt * background;

	gl_FragColor = vec4(col, 1.0);
}
