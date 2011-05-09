/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.derivative.sobel;

import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageInt16;
import gecv.struct.image.ImageInt8;

/**
 * <p>
 * This implementation of the sobel edge dector is implements it in such as way that the code can be easily read
 * and verified for correctness, however it is much slower than it needs to be.  The intended purpose of this
 * class is to compare it against others.
 * </p>
 * <p>
 * This code is being saved to avoid repeating past work and make it easier to understand other implementations.
 * </p>
 *
 * @author Peter Abeles
 * @see gecv.alg.filter.derivative.GradientSobel
 */
public class GradientSobel_Naive {

	/**
	 * Computes the derivative of 'orig' along the x and y axes
	 */
	public static void process_I8(ImageInt8 orig,
								  ImageInt16 derivX,
								  ImageInt16 derivY) {
		final int width = orig.getWidth();
		final int height = orig.getHeight();

		for (int y = 1; y < height - 1; y++) {

			for (int x = 1; x < width - 1; x++) {

				int dy = -(orig.get(x - 1, y - 1) + 2 * orig.get(x, y - 1) + orig.get(x + 1, y - 1));
				dy += (orig.get(x - 1, y + 1) + 2 * orig.get(x, y + 1) + orig.get(x + 1, y + 1));


				int dx = -(orig.get(x - 1, y - 1) + 2 * orig.get(x - 1, y) + orig.get(x - 1, y + 1));
				dx += (orig.get(x + 1, y - 1) + 2 * orig.get(x + 1, y) + orig.get(x + 1, y + 1));

				derivX.set(x, y, (short) dx);
				derivY.set(x, y, (short) dy);
			}
		}
	}

	/**
	 * Computes the derivative of 'orig' along the x and y axes
	 */
	public static void process_F32(ImageFloat32 orig,
								   ImageFloat32 derivX,
								   ImageFloat32 derivY) {
		final int width = orig.getWidth();
		final int height = orig.getHeight();

		for (int y = 1; y < height - 1; y++) {

			for (int x = 1; x < width - 1; x++) {

				float dy = -(orig.get(x - 1, y - 1) * 0.25F + orig.get(x, y - 1) * 0.5F + orig.get(x + 1, y - 1) * 0.25F);
				dy += (orig.get(x - 1, y + 1) * 0.25F + orig.get(x, y + 1) * 0.5F + orig.get(x + 1, y + 1) * 0.25F);


				float dx = -(orig.get(x - 1, y - 1) * 0.25F + orig.get(x - 1, y) * 0.5F + orig.get(x - 1, y + 1) * 0.25F);
				dx += (orig.get(x + 1, y - 1) * 0.25F + orig.get(x + 1, y) * 0.5F + orig.get(x + 1, y + 1) * 0.25F);

				derivX.set(x, y, dx);
				derivY.set(x, y, dy);
			}
		}
	}

}