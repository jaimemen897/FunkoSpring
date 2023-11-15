db.createCollection('orders');

db = db.getSiblingDB('orders');

db.orders.insertMany([
    {
        _id: ObjectId('6536518de9b0d305f193b5ef'),
        idUser: 1,
        client: {
            fullName: 'Juan Perez',
            email: 'juanperez@gmail.com',
            phoneNumber: '+34123456789',
            direction: {
                street: 'Calle Mayor',
                number: '10',
                city: 'Madrid',
                state: 'Madrid',
                country: 'España',
                zipCode: '28001',
            },
        },
        orderLines: [
            {
                idFunko: 2,
                quantity: 1,
                price: 19.99,
                total: 19.99,
            },
            {
                idFunko: 3,
                quantity: 2,
                price: 15.99,
                total: 31.98,
            },
        ],
        createdAt: '2023-10-23T12:57:17.3411925',
        updatedAt: '2023-10-23T12:57:17.3411925',
        isDeleted: false,
        totalItems: 3,
        total: 51.97,
        _class: 'Order',
    },
]);