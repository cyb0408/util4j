package net.jueb.util4j.beta.tools.sbuffer.node;

/**
 * 向右分层(适合index逐渐增大的情况) 2个比特位=4
 * @author juebanlin
 */
public class Node_bit2 {
	/**
	 * 2进制的11
	 */
	static final int MASK=0x3;//截取掩码
	private static final int maxPos=16;//16*2位
	private static final int maskLen;//掩码长度
	static{
		int tmp=MASK;
		int num=0;
		while(tmp!=0)
		{
			tmp=tmp>>1;
			num++;
		}
		maskLen=num;
	}
	
	private Node_bit2[] sub=new Node_bit2[4];
	private Byte attach;//附件
	
	private Byte _get(int index,int pos)
	{
		if(pos<0)
		{//超出范围
			return null;
		}
		if(pos==0)
		{
			return this.attach;
		}
		pos--;
		int p=getPosValue(index,pos);
		Node_bit2 node=sub[p];
		if(node==null)
		{
			return null;
		}
		return node._get(index, pos);
	}
	
	public Byte get(int index)
	{
		return _get(index,maxPos);
	}
	
	public static int num;
	
	private void _set(int index,int pos,Byte value)
	{
		if(pos<0)
		{//超出范围
			return ;
		}
		if(pos==0)
		{
			this.attach=value;
			return ;
		}
		pos--;
		int p=getPosValue(index,pos);
		Node_bit2 node=sub[p];
		if(node==null)
		{
			node=new Node_bit2();
			sub[p]=node;
		}
		node._set(index, pos,value);
	}
	
	public void set(int index,Byte value)
	{
		_set(index,maxPos,value);
	}
	
	public void put(int index,Byte value)
	{
		set(index,value);
	}
	
	/**
	 * 取整数某二进制位的值
	 * @param number
	 * @param pos 0开始
	 * @return
	 */
	public int getPosValue(int number,int layout)
	{
		int pos=maskLen*layout;
		int posValue=(number & (MASK<<pos))>>>pos;
		return posValue;
	}
	
	public static class Test{
		
		public void test0()
		{
			String a = "10010101";  
		    int d = Integer.parseInt(a, 2); // 2进制  
		    System.out.println(d);  
		    for(int i=0;i<32;i++)
		    {
		    	System.out.println((d & (1<<i))>>i);
		    }
		}
		
		public void test1()
		{
			Node_bit2 node=new Node_bit2();
			int key=1001;
		    Byte value=123;
		    node.set(key,value);
		    Byte value2=node.get(key);
		    System.out.println(value2);  	
		}
		public void test2()
		{
			Node_bit2 node=new Node_bit2();
			int num=100000;
			long t=System.currentTimeMillis();
		    for(int i=0;i<num;i++)
		    {
		    	int key=i;
		    	Byte v=(byte) key;
		    	node.set(key,v);
		    }	
		    long t1=System.currentTimeMillis()-t;
		    t=System.currentTimeMillis();
		    int error=0;
		    for(int i=0;i<num;i++)
		    {
		    	int key=i;
		    	byte value=node.get(key);
		    	if(value!=(byte)key)
		    	{
		    		error++;
		    	}
		    }
		    long t2=System.currentTimeMillis()-t;
		    System.out.println("set:"+t1+",get:"+t2+",error="+error);
		}
	}
	
	public static void main(String[] args) {
	    new Test().test2();
	}
}
