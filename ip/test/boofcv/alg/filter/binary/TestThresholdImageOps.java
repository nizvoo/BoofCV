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

package boofcv.alg.filter.binary;

import boofcv.core.image.FactorySingleBandImage;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.core.image.SingleBandImage;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;
import boofcv.testing.BoofTesting;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestThresholdImageOps {

	int width = 20;
	int height = 30;

	@Test
	public void threshold() {

		int total = 0;
		Method[] list = ThresholdImageOps.class.getMethods();

		for( Method m : list ) {
			if( !m.getName().equals("threshold"))
				continue;

			Class<?> param[] = m.getParameterTypes();

			ImageBase input = GeneralizedImageOps.createImage(param[0],width,height);
			ImageUInt8 output = new ImageUInt8(width,height);

			SingleBandImage a = FactorySingleBandImage.wrap(input);
			for( int y = 0; y < input.height; y++ ) {
				for( int x = 0; x < input.width; x++ ) {
					a.set(x,y,x);
				}
			}

			BoofTesting.checkSubImage(this,"performThreshold",true,m,input,output);
			total++;
		}

		assertEquals(6,total);
	}

	public void performThreshold( Method m , ImageBase input , ImageUInt8 output )
			throws InvocationTargetException, IllegalAccessException
	{
		m.invoke(null,input,output,7,true);
		assertEquals(240, GeneralizedImageOps.sum(output),1e-4);

		m.invoke(null,input,output,7,false);
		assertEquals(390, GeneralizedImageOps.sum(output),1e-4);
	}

	@Test
	public void thresholdBlobs() {
		int total = 0;
		Method[] list = ThresholdImageOps.class.getMethods();

		for( Method m : list ) {
			if( !m.getName().equals("thresholdBlobs"))
				continue;

			Class<?> param[] = m.getParameterTypes();

			ImageBase input = GeneralizedImageOps.createImage(param[0],width,height);
			ImageSInt32 labeled = new ImageSInt32(width,height);

			SingleBandImage a = FactorySingleBandImage.wrap(input);
			for( int y = 0; y < input.height; y++ ) {
				for( int x = 0; x < input.width; x++ ) {
					a.set(x,y,x);
					labeled.set(x,y,x);
				}
			}

			BoofTesting.checkSubImage(this,"performThresholdBlobs",true,m,input,labeled);
			total++;
		}

		assertEquals(6,total);
	}

	public void performThresholdBlobs( Method m , ImageBase input , ImageSInt32 labeled )
			throws InvocationTargetException, IllegalAccessException
	{
		int results[] = new int[width];
		m.invoke(null,input,labeled,results,width,7,true);
		for( int i = 0; i <= 7; i++ ) {
			assertEquals(i,results[i]);
		}
		for( int i = 8; i < width; i++ ) {
			assertEquals(0,results[i]);
		}

		m.invoke(null,input,labeled,results,width,7,false);
		for( int i = 0; i < 7; i++ ) {
			assertEquals(0,results[i]);
		}
		for( int i = 7; i < width; i++ ) {
			assertEquals(i,results[i]);
		}
	}
}