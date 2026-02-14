import QRCode from 'qrcode';
import { uploadToCloudinary } from './cloudinary';

/**
 * Generates a QR code from a URL/text and uploads it to Cloudinary
 * @param {string} data - The URL or text to encode in the QR code
 * @param {object} options - QR code generation options
 * @returns {Promise<string>} - Cloudinary URL of the uploaded QR code
 */
export const generateAndUploadQRCode = async (data, options = {}) => {
    try {
        // Default QR code options
        const qrOptions = {
            errorCorrectionLevel: 'H', // High error correction
            type: 'image/png',
            quality: 0.95,
            margin: 1,
            width: 512, // 512x512 pixels
            color: {
                dark: options.darkColor || '#000000',
                light: options.lightColor || '#FFFFFF'
            },
            ...options
        };

        // Generate QR code as Data URL
        const qrDataUrl = await QRCode.toDataURL(data, qrOptions);
        
        // Convert Data URL to Blob
        const blob = await dataURLToBlob(qrDataUrl);
        
        // Create a File object from the Blob
        const file = new File([blob], `qr-code-${Date.now()}.png`, { type: 'image/png' });
        
        // Upload to Cloudinary
        const cloudinaryUrl = await uploadToCloudinary(file);
        
        return cloudinaryUrl;
    } catch (error) {
        console.error('Error generating/uploading QR code:', error);
        throw new Error('Failed to generate and upload QR code');
    }
};

/**
 * Generates a QR code and returns it as a Data URL (for preview)
 * @param {string} data - The URL or text to encode
 * @param {object} options - QR code options
 * @returns {Promise<string>} - Data URL of the QR code
 */
export const generateQRCodeDataURL = async (data, options = {}) => {
    try {
        const qrOptions = {
            errorCorrectionLevel: 'H',
            type: 'image/png',
            quality: 0.95,
            margin: 1,
            width: 512,
            color: {
                dark: options.darkColor || '#000000',
                light: options.lightColor || '#FFFFFF'
            },
            ...options
        };

        return await QRCode.toDataURL(data, qrOptions);
    } catch (error) {
        console.error('Error generating QR code:', error);
        throw new Error('Failed to generate QR code');
    }
};

/**
 * Generates a QR code as a canvas element (for direct rendering)
 * @param {HTMLCanvasElement} canvas - Canvas element to render to
 * @param {string} data - The URL or text to encode
 * @param {object} options - QR code options
 */
export const generateQRCodeToCanvas = async (canvas, data, options = {}) => {
    try {
        const qrOptions = {
            errorCorrectionLevel: 'H',
            margin: 1,
            width: 512,
            color: {
                dark: options.darkColor || '#000000',
                light: options.lightColor || '#FFFFFF'
            },
            ...options
        };

        await QRCode.toCanvas(canvas, data, qrOptions);
    } catch (error) {
        console.error('Error generating QR code to canvas:', error);
        throw new Error('Failed to generate QR code');
    }
};

/**
 * Helper function to convert Data URL to Blob
 * @param {string} dataUrl - Data URL to convert
 * @returns {Promise<Blob>}
 */
const dataURLToBlob = (dataUrl) => {
    return new Promise((resolve, reject) => {
        try {
            const arr = dataUrl.split(',');
            const mime = arr[0].match(/:(.*?);/)[1];
            const bstr = atob(arr[1]);
            let n = bstr.length;
            const u8arr = new Uint8Array(n);
            
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            
            resolve(new Blob([u8arr], { type: mime }));
        } catch (error) {
            reject(error);
        }
    });
};

/**
 * Downloads a QR code as a PNG file
 * @param {string} dataUrl - Data URL of the QR code
 * @param {string} filename - Name for the downloaded file
 */
export const downloadQRCode = (dataUrl, filename = 'qr-code.png') => {
    const link = document.createElement('a');
    link.href = dataUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};