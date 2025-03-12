package comp3170.week3;

import static comp3170.Math.TAU;

import static org.lwjgl.opengl.GL11.GL_FILL;
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
	
	private Vector3f[] colours;
	private int colourBuffer;

	private Shader shader;
	
	private Matrix4f[] modelMatrixBuffer;
	//private Matrix4f rotMatrix;
	//private Matrix4f transMatrix;
	//private Matrix4f scalMatrix;
	
	private int NPLANES = 10;
	private Vector4f[] position;

	public Plane(int nPlanes) {
		this.NPLANES = nPlanes;

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

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

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
		};
		// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
		
		for (int i = 0; i < NPLANES; i++) {
			
			Matrix4f modelMatrix = new Matrix4f();
			Matrix4f transMatrix = new Matrix4f();
			Matrix4f rotMatrix = new Matrix4f();
			Matrix4f scalMatrix = new Matrix4f();
			
			modelMatrix.identity();
			
			float x = (float) Math.random() * 2 - 1;
			float y = (float) Math.random() * 2 - 1;
			translationMatrix(x, y, transMatrix);
			rotationMatrix(TAU/3, rotMatrix);
			scaleMatrix(0.1f,0.1f, scalMatrix);
			
			modelMatrix.mul(transMatrix).mul(rotMatrix).mul(scalMatrix);
			modelMatrixBuffer[i] = modelMatrix;
			
			//Color c = Color.getHSBColor((float) Math.random(), 1, 1);
			//colour[i] = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		}
		
		
		
		position = new Vector4f[NPLANES];
		for (int i = 0; i < NPLANES; i++) {
			position[i] = new Vector4f( i*0.1f, i*0.1f, 0, 1);
			position[i] = new Vector4f( i*0.1f, i*0.1f, 0, 1);
		}
	}
	
	private final float MOVEMENT_SPEED = 10.0f;
	private final float ROTATION_SPEED = TAU/4;
	private final float SCALE_SPEED = 0.1f;
	
	public void update(float deltaTime) {

		float movement = MOVEMENT_SPEED * deltaTime;
		float rotation = ROTATION_SPEED * deltaTime;
		float scale = (float) Math.pow(SCALE_SPEED, deltaTime);
		
		for (int i = 0; i < NPLANES; i++) {
			Matrix4f modelMatrix = new Matrix4f();
			Matrix4f transMatrix = new Matrix4f();
			Matrix4f rotMatrix = new Matrix4f();
			Matrix4f scalMatrix = new Matrix4f();
			
			translationMatrix(0.0f, movement, transMatrix);
			rotationMatrix(rotation, rotMatrix);
			scaleMatrix(scale, scale, scalMatrix);
			
			modelMatrix.mul(rotMatrix).mul(transMatrix);
			modelMatrixBuffer[i] = modelMatrix;
		
		}
		
		

		
	}
	
	

	public void draw() {
		
		shader.enable();
		
		// set the attributes
		shader.setAttribute("a_colour", colourBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 1);
		
		shader.setAttribute("a_position", vertexBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_position"), 1);
		
		shader.setUniform("u_modelMatrix", modelMatrix);
		
		// pass the positions of every instance as an attribute
		int positionBuffer = GLBuffers.createBuffer(position);
		shader.setAttribute("a_worldPos", positionBuffer);
		// tell OpenGL this attribute is instanced
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 1);
		// draw all the instances at once
		glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, NPLANES);
		
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
