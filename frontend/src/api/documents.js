import api from './axios';

export const documentsApi = {
  upload: async ({ file, docType }) => {
    const fd = new FormData();
    fd.append('file', file);
    fd.append('docType', docType);
    return api.post('/accounts/documents/upload', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

