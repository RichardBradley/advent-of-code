package y2019;

import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * https://stackoverflow.com/questions/37963625/how-to-encode-images-into-a-video-file-in-java-through-programming/38015682#38015682
 */
class VideoWriter {

    final MediaPacket packet;
    private final Codec codec;
    private final Rational framerate;
    private Encoder encoder;
    private final MuxerFormat format;
    private final Muxer muxer;
    private MediaPictureConverter converter;
    private MediaPicture picture;
    int frameNumber = 0;

    public VideoWriter() {

        int framesPerSecond = 30;
        framerate = Rational.make(1, framesPerSecond);
        String filename = "out.mp4";
        muxer = Muxer.make(filename, null, null);
        format = muxer.getFormat();
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        packet = MediaPacket.make();
    }

    public void finish() {
        /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
         * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
         * input until the output is not complete.
         */
        do
        {
            encoder.encode(packet, null);
            if (packet.isComplete())
            {
                muxer.write(packet, false);
            }
        } while (packet.isComplete());

        /** Finally, let's clean up after ourselves. */
        muxer.close();
    }

    public void putFrame(BufferedImage image) throws IOException, InterruptedException {
        if (encoder == null) {
            initEncoderFromFirstFrame(image);
        }

        converter.toPicture(picture, image, frameNumber++);
        do
        {
            encoder.encode(packet, picture);
            if (packet.isComplete())
            {
                muxer.write(packet, false);
            }
        } while (packet.isComplete());
    }

    private void initEncoderFromFirstFrame(BufferedImage image) throws InterruptedException, IOException {
        encoder = Encoder.make(codec);
        encoder.setWidth(image.getWidth(null));
        encoder.setHeight(image.getHeight(null));
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
         * and in that case you have to tell the encoder. And since Encoders are decoupled from
         * Muxers, there is no easy way to know this beyond
         */
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
        {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }

        encoder.open(null, null);

        muxer.addNewStream(encoder);

        /** And open the muxer for business. */
        muxer.open(null, null);

        /** Next, we need to make sure we have the right MediaPicture format objects
         * to encode data with. Java (and most on-screen graphics programs) use some
         * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
         * codecs use some variant of YCrCb formatting. So we're going to have to
         * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
         */
        picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
        picture.setTimeBase(framerate);
        converter = MediaPictureConverterFactory.createConverter(image, picture);
    }
}
