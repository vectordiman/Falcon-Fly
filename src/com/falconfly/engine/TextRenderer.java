package com.falconfly.engine;

import com.falconfly.engine.input.FileReader;
import com.falconfly.menu.MenuStorageLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

class TextRenderer {
	private final ByteBuffer ttf;
	private final STBTTFontinfo info;

	private final int ascent;
	private final int descent;
	private final int lineGap;

	private static final int MAX_BUFFER_SIZE = 1024 * 512;
	private static final int BITMAP_W = 512;
	private static final int BITMAP_H = 512;

	private static int fontSize = 24;

	TextRenderer() {
		ttf = LoadFont(new MenuStorageLoader().Load("fonts").get(0).substring(8));
		info = STBTTFontinfo.create();

		if (!stbtt_InitFont(info, ttf)) {
			throw new IllegalStateException("Failed to initialize font information.");
		}

		try (MemoryStack stack = stackPush()) {
			IntBuffer pAscent  = stack.mallocInt(1);
			IntBuffer pDescent = stack.mallocInt(1);
			IntBuffer pLineGap = stack.mallocInt(1);

			stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

			ascent = pAscent.get(0);
			descent = pDescent.get(0);
			lineGap = pLineGap.get(0);
		}
	}

	public ByteBuffer LoadFont(String resource) {
		FileReader fr = new FileReader();
		return fr.getResource(resource, MAX_BUFFER_SIZE);
	}

	public STBTTBakedChar.Buffer Init() {
		int texID = glGenTextures();
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

		ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
		stbtt_BakeFontBitmap(ttf, fontSize, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

		glBindTexture(GL_TEXTURE_2D, texID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		return cdata;
	}

	public void setTextColor(int r, int g, int b) {
		glColor3f(r / 255f, g / 255f, b / 255f);
	}

	public void DrawString(float dx, float dy, String text, STBTTBakedChar.Buffer cdata) {
		glPushMatrix();
		float scaleFactor = 1.0f + 0 * 0.25f;
		glScalef(scaleFactor, scaleFactor, 1f);
		glTranslatef(4.0f, 20.0f, 0f);

		float scale = stbtt_ScaleForPixelHeight(info, fontSize);

		try (MemoryStack stack = stackPush()) {
			IntBuffer pCodePoint = stack.mallocInt(1);

			FloatBuffer x = stack.floats(dx);
			FloatBuffer y = stack.floats(dy);

			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

			int lineStart = 0;

			float factorX = 1.0f;
			float factorY = 1.0f;

			float lineY = 0.0f;

			glBegin(GL_QUADS);
			for (int i = 0, to = text.length(); i < to; ) {
				i += getCP(text, to, i, pCodePoint);

				int cp = pCodePoint.get(0);
				if (cp == '\n') {
					y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
					x.put(0, 0.0f);

					lineStart = i;
					continue;
				} else if (cp < 32 || 128 <= cp) {
					continue;
				}

				float cpX = x.get(0);
				stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);
				x.put(0, scale(cpX, x.get(0), factorX));
				if (i < to) {
					getCP(text, to, i, pCodePoint);
					x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)) * scale);
				}

				float
						x0 = scale(cpX, q.x0(), factorX),
						x1 = scale(cpX, q.x1(), factorX),
						y0 = scale(lineY, q.y0(), factorY),
						y1 = scale(lineY, q.y1(), factorY);

				glTexCoord2f(q.s0(), q.t0());
				glVertex2f(x0, y0);

				glTexCoord2f(q.s1(), q.t0());
				glVertex2f(x1, y0);

				glTexCoord2f(q.s1(), q.t1());
				glVertex2f(x1, y1);

				glTexCoord2f(q.s0(), q.t1());
				glVertex2f(x0, y1);
			}
			glEnd();
		}
		glPopMatrix();
	}

	private void renderLineBB(int from, int to, float y, float scale, String text) {
		glDisable(GL_TEXTURE_2D);
		glPolygonMode(GL_FRONT, GL_LINE);
		glColor3f(1.0f, 1.0f, 0.0f);

		float width = getStringWidth(info, text, from, to, fontSize);
		y -= descent * scale;

		glBegin(GL_QUADS);
		glVertex2f(0.0f, y);
		glVertex2f(width, y);
		glVertex2f(width, y - fontSize);
		glVertex2f(0.0f, y - fontSize);
		glEnd();

		glEnable(GL_TEXTURE_2D);
		glPolygonMode(GL_FRONT, GL_FILL);
		glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
	}

	private float getStringWidth(STBTTFontinfo info, String text, int from, int to, int fontHeight) {
		int width = 0;

		try (MemoryStack stack = stackPush()) {
			IntBuffer pCodePoint       = stack.mallocInt(1);
			IntBuffer pAdvancedWidth   = stack.mallocInt(1);
			IntBuffer pLeftSideBearing = stack.mallocInt(1);

			int i = from;
			while (i < to) {
				i += getCP(text, to, i, pCodePoint);
				int cp = pCodePoint.get(0);

				stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
				width += pAdvancedWidth.get(0);

				if (i < to) {
					getCP(text, to, i, pCodePoint);
					width += stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0));
				}
			}
		}

		return width * stbtt_ScaleForPixelHeight(info, fontHeight);
	}

	private static int getCP(String text, int to, int i, IntBuffer cpOut) {
		char c1 = text.charAt(i);
		if (Character.isHighSurrogate(c1) && i + 1 < to) {
			char c2 = text.charAt(i + 1);
			if (Character.isLowSurrogate(c2)) {
				cpOut.put(0, Character.toCodePoint(c1, c2));
				return 2;
			}
		}
		cpOut.put(0, c1);
		return 1;
	}

	private static float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}
}