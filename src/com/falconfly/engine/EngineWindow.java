package com.falconfly.engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;

public class EngineWindow {
    // Window identification
    public long id;

    // Maximal values
    private int MAX_WIDTH = 600;
    private int MAX_HEIGHT = 400;

    private String title;
    private int width;
    private int height;
    private boolean vSync;

    private GLFWVidMode videomode;
    public IntBuffer bufferedWidth;
    public IntBuffer bufferedHeight;

    // Singleton
    public static EngineWindow windowInstance;


    public EngineWindow(String title, int width, int height, boolean vSync) {
        windowInstance = this;
        this.width = width;
        this.height = height;
        this.title = title;
        this.vSync = vSync;
    }

    private void windowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void create() {
        if (!GLFW.glfwInit()) {
            System.err.println("GLFW cannot initialize");
            System.exit(-1);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        this.id = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0, 0);
        if (this.id == 0) {
            System.err.println("GLFW cannot create window");
            System.exit(-1);
        }

        try(MemoryStack memory = MemoryStack.stackPush()) {
            this.bufferedWidth = BufferUtils.createIntBuffer(1);
            this.bufferedHeight = BufferUtils.createIntBuffer(1);
            GLFW.glfwGetWindowSize(this.id, this.bufferedWidth, this.bufferedHeight);
        } catch (Exception e) {
            System.err.println("Cannot alloc memory");
            System.exit(-1);
        }
        // Getting video mode of primary monitor
        this.videomode = GLFW.glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowSizeCallback(this.id, this::windowSizeChanged);

        // Setting primitives
        GLFW.glfwSetWindowTitle(this.id, this.title);
        GLFW.glfwSetWindowSize(this.id, this.width, this.height);


        // Creating context
        glfwMakeContextCurrent(this.id);
        GL.createCapabilities();

        // Creating window viewport: x, y - offset
        GL11.glViewport(0, 0, this.bufferedWidth.get(0), this.bufferedHeight.get(0));


        // For text renderer
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);

        // Enables depth
        glEnable(GL_DEPTH_TEST);

        if (this.vSync)
            glfwSwapInterval(1);
        glfwShowWindow(id);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public void update() {
        // Clearing window each update
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(this.id);
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(this.id);
    }

    public boolean isCloseRequest() {
        return GLFW.glfwWindowShouldClose(this.id);
    }

    public static EngineWindow getWindowInstance() {
        return windowInstance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }
}
