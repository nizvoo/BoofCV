/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.sfm.d3.direct;

import boofcv.abst.sfm.ImagePixelTo3D;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.struct.image.ImageMultiBand;
import boofcv.struct.image.ImageType;
import boofcv.struct.pyramid.ImagePyramid;
import georegression.struct.se.Se3_F32;

/**
 * TODO write
 *
 * @author Peter Abeles
 */
public class PyramidDirectRgbDepth<T extends ImageMultiBand<T>> {

	ImageType<T> imageType;

	ImagePyramid<T> pyramid;
	VisOdomDirectRgbDepth<T,?>[] layersOdom;

	LayerTo3D layerTo3D;

	Se3_F32 keyToCurrent = new Se3_F32();
	Se3_F32 work = new Se3_F32();

	public PyramidDirectRgbDepth(ImagePyramid<T> pyramid) {
		this.pyramid = pyramid;
		imageType = this.pyramid.getImageType();

		layersOdom = new VisOdomDirectRgbDepth[pyramid.getNumLayers()];
		for (int i = 0; i < layersOdom.length; i++) {
			ImageType derivType = GImageDerivativeOps.getDerivativeType( imageType );
			layersOdom[i] = new VisOdomDirectRgbDepth(pyramid.getImageType(),derivType );
		}
	}

	public void setCameraParameters(float fx , float fy ,
									float cx , float cy ,
									int width , int height )
	{
		pyramid.initialize(width, height);
		for (int layer = 0; layer < layersOdom.length; layer++) {
			VisOdomDirectRgbDepth o = layersOdom[layer];

			float scale = (float)pyramid.getScale(layer);

			o.setCameraParameters(fx/scale,fy/scale,cx/scale,cy/scale,
					(int)Math.round(width/scale),(int)Math.round(height/scale));
		}
	}

	public void setKeyFrame( T input , ImagePixelTo3D inputDepth) {
		pyramid.process(input);
		layerTo3D.wrap(inputDepth);

		for (int layer = 0; layer < layersOdom.length; layer++) {
			T layerImage = pyramid.getLayer(layer);
			layerTo3D.scale = pyramid.getScale(layer);
			layersOdom[layer].setKeyFrame(layerImage,layerTo3D);
		}

	}

	public boolean estimateMotion( T input ) {
		pyramid.process(input);
		work.set(keyToCurrent);

		for (int layer = layersOdom.length-1; layer >= 0; layer--) {
			T layerImage = pyramid.getLayer(layer);
			VisOdomDirectRgbDepth<T,?> o = layersOdom[layer];
			if( o.estimateMotion(layerImage, work) ) {
				work.set( o.getKeyToCurrent() );
			} else {
				return false;
			}

		}
		return true;
	}

	public static class LayerTo3D implements ImagePixelTo3D {
		ImagePixelTo3D orig;

		public double scale;

		public void wrap(ImagePixelTo3D orig) {
			this.orig = orig;
		}

		@Override
		public boolean process(double x, double y) {
			return orig.process(x*scale, y*scale);
		}

		@Override
		public double getX() {
			return orig.getX();
		}

		@Override
		public double getY() {
			return orig.getY();
		}

		@Override
		public double getZ() {
			return orig.getZ();
		}

		@Override
		public double getW() {
			return orig.getW();
		}
	}

}