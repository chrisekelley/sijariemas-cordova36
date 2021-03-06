package sourceafis.general;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import org.rti.kidsthrive.secugenplugin.SecugenPlugin;

import sourceafis.meta.Action;
import sourceafis.meta.ObjectTree;

public class DetailLogger {
	public static final Hook off = new NullHook();
	private LogData currentLog = new LogData();

	public static void log2D(byte[][] image, String fileName) {
		PrintWriter file_m = null;
		try {
			file_m = new PrintWriter(SecugenPlugin.getTemplatePath() + fileName);
			for (int y = 0; y < image.length; y++) {
				for (int x = 0; x < image[0].length; x++) {
					file_m.print((image[y][x] & 0xFF) + ",");
				}
				file_m.println();
			}
			file_m.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void log3D(short[][][] histogram, String fileName) {
		try {
			PrintWriter file_m = new PrintWriter(SecugenPlugin.getTemplatePath() + fileName);
			for (int y = 0; y < histogram.length; y++) {
				for (int x = 0; x < histogram[0].length; x++) {
					for (int k = 0; k < histogram[0][0].length; k++) {
						file_m.print((histogram[y][x][k]) + ",");
					}
					file_m.println();
				}
				file_m.println();
			}
			file_m.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void log(BinaryMap image, String fileName) {
		PrintWriter file_m = null;
		try {
			file_m = new PrintWriter(SecugenPlugin.getTemplatePath() + fileName);
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					file_m.print(image.GetBit(x, y) + ",");
				}
				file_m.println();
			}
			file_m.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void attach(ObjectTree tree) {
		try {
			for (Object reference : tree.getAllObjects())
				for (Field field : reference.getClass().getFields())
					if (field.getName().equals("logger"))
						field.set(reference,
								new ActiveHook(tree.getPath(reference)));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void log(String path, Object data) {
		Object logged = data;
		try {
			for (Method clone : data.getClass().getMethods())
				if (clone.getName().equals("clone")
						&& Modifier.isPublic(clone.getModifiers()))
					logged = clone.invoke(data);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		currentLog.append(path, logged);
	}

	public LogData popLog() {
		LogData result = currentLog;
		currentLog = new LogData();
		return result;
	}

	public static void copyHooks(Object original, Object copy) {
		ObjectTree originalTree = new ObjectTree(original);
		ObjectTree copyTree = new ObjectTree(copy);
		for (Object originalRef : originalTree.getAllObjects())
			for (Field field : originalRef.getClass().getFields())
				if (field.getName().equals("logger")) {
					Object copyRef = copyTree.getObject(originalTree
							.getPath(originalRef));
					try {
						field.set(copyRef, field.get(originalRef));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
	}

	public static abstract class Hook {
		public abstract boolean isActive();

		public abstract void log(Object data);

		public abstract void log(String part, Object data);
	}

	private static class NullHook extends Hook {
		@Override
		public boolean isActive() {
			return false;
		}

		@Override
		public void log(Object data) {
		}

		@Override
		public void log(String part, Object data) {
		}
	}

	private class ActiveHook extends Hook implements Cloneable {
		private String path;

		public ActiveHook(String p) {
			path = p;
		}

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void log(Object data) {
			DetailLogger.this.log(path, data);
		}

		@Override
		public void log(String part, Object data) {
			DetailLogger.this.log(path + (path != "" ? "." : "") + part, data);
		}
	}

	public class LogData {
		private HashMap<String, ArrayList<Object>> history = new HashMap<String, ArrayList<Object>>();

		public void append(String path, Object data) {
			if (!history.containsKey(path))
				history.put(path, new ArrayList<Object>());
			history.get(path).add(data);
		}

		public int count(String path) {
			if (history.containsKey(path))
				return history.get(path).size();
			else
				return 0;
		}

		public Object retrieve(String path, int index) {
			if (history.containsKey(path) && index < history.get(path).size())
				return history.get(path).get(index);
			else
				return null;
		}

		public Object retrieve(String path) {
			return retrieve(path, 0);
		}
	}

	static ThreadLocal<String> ThreadName = new ThreadLocal<String>();

	static String GetThreadName() {
		if (ThreadName.get() != null)
			return "[" + ThreadName.get() + "]";
		else
			return "";
	}

	public static void RunInContext(String name, Action task) {
		ThreadName.set(name);
		try {
			task.function();
		} finally {
			ThreadName.set(null);
		}
	}

}
