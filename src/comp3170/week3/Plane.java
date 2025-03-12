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

public class Plane {

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
	private float[] scale;
	
	private int positionBuffer;
	private int rotationBuffer;
	private int scaleBuffer;
	private int colourBuffer;
	
	private float[] velocity;
	private int velocityBuffer;
	
	private int NPLANES = 10;

	public Plane(int nPlanes) {
		this.NPLANES = nPlanes;

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();

		// One transform per instance
		position = new Vector2f[NPLANES];
		angle = new float[NPLANES];
		scale = new float[NPLANES];
		colour = new Vector3f[NPLANES];
		
		velocity = new float[NPLANES];

		for (int i = 0; i < NPLANES; i++) {
			float x = (float) Math.random() * 2 - 1;
			float y = (float) Math.random() * 2 - 1;
			position[i] = new Vector2f(x, y);
			angle[i] = (float) Math.random() * 360;
			scale[i] = 0.01f;
			Color c = Color.getHSBColor((float) Math.random(), 1, 1);
			colour[i] = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
			velocity[i] = (float) Math.random()*5;
		}

		// create buffers for all the matrices and colours
		positionBuffer = GLBuffers.createBuffer(position);
		rotationBuffer = GLBuffers.createBuffer(angle, GL_FLOAT);
		scaleBuffer = GLBuffers.createBuffer(scale, GL_FLOAT);
		colourBuffer = GLBuffers.createBuffer(colour);
		
		velocityBuffer = GLBuffers.createBuffer(velocity, GL_FLOAT);
		

	}
	
	private void makeMesh() {
		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
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
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
		};
		// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	

	
	private static final Vector2f MOVEMENT_SPEED = new Vector2f(0.0f, 0.0f);
	private static final float ROTATION_SPEED = TAU / 1.5f;
	private static final float SCALE_SPEED = 1.0f;
	private Vector2f movement = new Vector2f();

	public void update(float dt) {
		// update all the squares
		MOVEMENT_SPEED.mul(dt, movement); // movement = speed * dt

		for (int i = 0; i < position.length; i++) {
			position[i].add(movement);
			angle[i] = (angle[i] + ROTATION_SPEED * dt) % TAU;
			scale[i] = scale[i] * (float) Math.pow(SCALE_SPEED, dt);
		}

		// update the data in the buffers
		GLBuffers.updateBuffer(positionBuffer, position);
		GLBuffers.updateBuffer(rotationBuffer, angle, GL_FLOAT);
		GLBuffers.updateBuffer(scaleBuffer, scale, GL_FLOAT);

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
