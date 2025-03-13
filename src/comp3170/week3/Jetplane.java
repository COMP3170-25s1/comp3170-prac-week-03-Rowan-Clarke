package comp3170.week3;

import static comp3170.Math.TAU;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.awt.Color;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Jetplane {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f[] colour;
	private Shader shader;
	
	private Matrix4f[] modelMatrixes;
	private Vector2f[] position;
	private float[] angle;
	private Vector2f[] scale;
	
	private int positionBuffer;
	private int rotationBuffer;
	private int scaleBuffer;
	private int colourBuffer;
	
	private float[] velocity;
	private int velocityBuffer;
	
	private float[] rotSpeed;

	
	private int NPLANES = 10;

	public Jetplane(int nPlanes) {
		this.NPLANES = nPlanes;

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();

		// One transform per instance
		position = new Vector2f[NPLANES];
		angle = new float[NPLANES];
		scale = new Vector2f[NPLANES];
		colour = new Vector3f[NPLANES];
		
		velocity = new float[NPLANES];
		rotSpeed = new float[NPLANES];


		for (int i = 0; i < NPLANES; i++) {
			float x = (float) Math.random() * 2 - 1;
			float y = (float) Math.random() * 2 - 1;
			position[i] = new Vector2f(x, y);
			angle[i] = (float) Math.random() * TAU;
			scale[i] = new Vector2f(0.05f, 0.05f);
			Color c = Color.getHSBColor((float) Math.random(), 1, 1);
			colour[i] = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
			rotSpeed[i] = ((float) Math.random()*2-1) * TAU;
			velocity[i] = (float) Math.random()*3+2;
			//if (rotSpeed[i] < 0) {
			//	velocity[i] *= -1;
			//}
			
			
			
			//velocity[i] = 0;
			
		}

		// create buffers for all the matrices and colours
		positionBuffer = GLBuffers.createBuffer(position);
		rotationBuffer = GLBuffers.createBuffer(angle, GL_FLOAT);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour);
		
		velocityBuffer = GLBuffers.createBuffer(velocity, GL_FLOAT);
		

	}
	
	private void makeMesh() {	
		vertices = new Vector4f[] {
			new Vector4f( 0, 1, 0, 1), //P0
			new Vector4f( .1f, .9f, 0, 1), //P1
			new Vector4f( .2f, .7f, 0, 1), //P2
			new Vector4f( .2f, .5f, 0, 1), //P3
			new Vector4f( 1.2f, .3f, 0, 1), //P4
			new Vector4f( 1.2f, .2f, 0, 1), //P5
			new Vector4f( .2f, .2f, 0, 1), //P6
			new Vector4f( .2f, .1f, 0, 1), //P7
			new Vector4f( .1f, -.6f, 0, 1), //P8
			new Vector4f( .3f, -.7f, 0, 1), //P9
			new Vector4f( .3f, -.8f, 0, 1), //P10
			new Vector4f( .1f, -.8f, 0, 1), //P11
			new Vector4f( 0, -.9f, 0, 1), //P12
			new Vector4f( -.1f, -.8f, 0, 1), //P13
			new Vector4f( -.3f, -.8f, 0, 1), //P14
			new Vector4f( -.3f, -.7f, 0, 1), //P15
			new Vector4f( -.1f, -.6f, 0, 1), //P16
			new Vector4f( -.2f, .1f, 0, 1), //P17
			new Vector4f( -.2f, .2f, 0, 1), //P18
			new Vector4f( -1.2f, .2f, 0, 1), //P19
			new Vector4f( -1.2f, .3f, 0, 1), //P20
			new Vector4f( -.2f, .5f, 0, 1), //P21
			new Vector4f( -.2f, .7f, 0, 1), //P22
			new Vector4f( -.1f, .9f, 0, 1), //P23
			new Vector4f( -.8f, .2f, 0, 1), //P24
			new Vector4f( -.7f, .1f, 0, 1), //P25
			new Vector4f( -.6f, .2f, 0, 1), //P26
			new Vector4f( -.5f, .2f, 0, 1), //P27
			new Vector4f( -.4f, .1f, 0, 1), //P28
			new Vector4f( -.3f, .2f, 0, 1), //P29
			new Vector4f( .3f, .2f, 0, 1), //P30
			new Vector4f( .4f, .1f, 0, 1), //P31
			new Vector4f( .5f, .2f, 0, 1), //P32
			new Vector4f( .6f, .2f, 0, 1), //P33
			new Vector4f( .7f, .1f, 0, 1), //P34
			new Vector4f( .8f, .2f, 0, 1), //P35

		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);
		
		/*
		// @formatter:off
		colour = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colour);
		*/

		// @formatter:off
		indices = new int[] {  
			0, 1, 23,
			23, 1, 2,
			2, 23, 22,
			22, 2, 7,
			7, 22, 17,
			17, 7, 16,
			16, 7, 8,
			8, 9, 11,
			11, 10, 9,
			16, 8, 11,
			11, 12, 13,
			13, 11, 16,
			16, 15, 13,
			13, 14, 15, //End of main body
			3, 4, 6,
			6, 4, 5, //Right wing
			21, 20, 18,
			18, 19, 20, //Left wing
			24, 25, 26,
			27, 28, 29,
			30, 31, 32,
			33, 34, 35,
		};
		// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	

	
	private static final Vector2f MOVEMENT_SPEED = new Vector2f(0.0f, 0.0f);
	private static final float ROTATION_SPEED = TAU / 1.5f;
	//private static final float ROTATION_SPEED = 0;

	private static final float SCALE_SPEED = 1.0f;
	private Vector2f movement = new Vector2f();

	public void update(float dt) {
		// update all the squares
		MOVEMENT_SPEED.mul(dt, movement); // movement = speed * dt

		for (int i = 0; i < position.length; i++) {
			position[i].add(movement);
			angle[i] = (angle[i] + rotSpeed[i] * dt) % TAU;
			scale[i].x = scale[i].x * (float) Math.pow(SCALE_SPEED, dt);
			scale[i].y = scale[i].y * (float) Math.pow(SCALE_SPEED, dt);

		}

		// update the data in the buffers
		GLBuffers.updateBuffer(positionBuffer, position);
		GLBuffers.updateBuffer(rotationBuffer, angle, GL_FLOAT);
		GLBuffers.updateBuffer(scaleBuffer, scale);

	}
	
	

	public void draw() {
		
		shader.enable();

		// pass in all the model matrices
		shader.setAttribute("a_worldPos", positionBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 1);

		shader.setAttribute("a_rotation", rotationBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_rotation"), 1);

		shader.setAttribute("a_scale", scaleBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 1);

		// write the colour value into the u_colour uniform
		shader.setAttribute("a_colour", colourBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 1);
		
		shader.setAttribute("a_velocity", velocityBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_velocity"), 1);

		// connect the vertex buffer to the a_position attribute
		shader.setAttribute("a_position", vertexBuffer);

		// draw using an index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, NPLANES);

		// set back to non-instance based drawing
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_rotation"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_velocity"), 0);
		
	}

	private Matrix4f transformWorld(Matrix4f model, Matrix4f R, Matrix4f S, Matrix4f T) {
		model.mul(T).mul(R).mul(S);
		return model;
	}
	
	private Matrix4f transformLocal(Matrix4f model, Matrix4f R, Matrix4f S, Matrix4f T) {
		model.mul(R).mul(S).mul(T);
		return model;
	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		//dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		//dest.identity();
		
		//	   [ m00 m10 m20 m30 ]
		// T = [ m01 m11 m21 m31 ]
		//     [ m02 m12 m22 m32 ]
		//     [ m03 m13 m23 m33 ]
		
		dest.m00((float)Math.cos(angle));
		dest.m01((float)Math.sin(angle));
		dest.m10((float)Math.sin(-angle));
		dest.m11((float)Math.cos(angle));
		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {

		//dest.identity();
		
		//	   [ m00 m10 m20 m30 ]
		// T = [ m01 m11 m21 m31 ]
		//     [ m02 m12 m22 m32 ]
		//     [ m03 m13 m23 m33 ]
		
		dest.m00(sx);
		dest.m11(sy);
		return dest;
	}

}
