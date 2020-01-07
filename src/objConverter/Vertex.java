package objConverter;

//ThinMatrix's Vertex Class

import org.joml.Vector3f;

public class Vertex {
	
	private static final int NO_INDEX = -1;
	
	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private int index;
	private float length;
	
	public Vertex(int index,Vector3f position)
	{
		this.index = index;
		this.position = position;
		this.length = position.length();
	}
	
	public int GetIndex()
	{
		return index;
	}
	
	public float GetLength()
	{
		return length;
	}
	
	public boolean IsSet()
	{
		return textureIndex != NO_INDEX && normalIndex!=NO_INDEX;
	}
	
	public boolean HasSameTextureAndNormal(int textureIndexOther,int normalIndexOther)
	{
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}

	public Vector3f GetPosition()
	{
		return position;
	}

	public int GetTextureIndex()
	{
		return textureIndex;
	}

	public int GetNormalIndex()
	{
		return normalIndex;
	}

	public Vertex GetDuplicateVertex()
	{
		return duplicateVertex;
	}

	public void SetTextureIndex(int textureIndex)
	{
		this.textureIndex = textureIndex;
	}

	public void SetNormalIndex(int normalIndex)
	{
		this.normalIndex = normalIndex;
	}

	public void SetDuplicateVertex(Vertex duplicateVertex)
	{
		this.duplicateVertex = duplicateVertex;
	}

}
