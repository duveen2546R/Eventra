export const uploadToCloudinary = async (file) => {
    const formData = new FormData();

    formData.append("file", file);
    formData.append("upload_preset", "eventra_upload"); 
    formData.append("folder", "eventra_images");

    const res = await fetch(
        "https://api.cloudinary.com/v1_1/dbnknzmjg/image/upload",
        {
            method: "POST",
            body: formData
        }
    );

    const data = await res.json();
    return data.secure_url;
};