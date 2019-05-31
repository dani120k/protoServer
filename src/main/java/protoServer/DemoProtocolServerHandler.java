package protoServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import protoServer.DemoProtocol.DemoResponse.*;
import protoServer.DemoProtocol.AnyRequest.BookHotel;

import static protoServer.DemoProtocol.DemoResponse.newBuilder;

public class DemoProtocolServerHandler extends SimpleChannelInboundHandler<protoServer.DemoProtocol.AnyRequest> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, protoServer.DemoProtocol.AnyRequest msg)
      throws Exception {

      if (msg.hasHotelRequest()){
          Builder builder = handleGetHotelListRequest();
          ctx.write(builder.build());
      }

      if (msg.hasRoomsRequest()){
            Builder builder = handleGetRoomListRequest(msg.getRoomsRequest().getHotelId());
            ctx.write(builder.build());
      }

      if (msg.hasBookRequest()){
          Builder builder = handleBookHotelRequest(msg.getBookRequest());
          ctx.write(builder.build());
      }




    
  }

  private Builder handleGetHotelListRequest(){
      Builder builder = newBuilder();

      List<Hotel> hotels = createHotels();

      ResponseGetHotelList responseGetHotelList = ResponseGetHotelList.newBuilder().addAllHotels(hotels).build();

      System.out.println(responseGetHotelList.getHotelsCount());

      builder.setHotelRequest(responseGetHotelList);
      return builder;
  }

  private Builder handleBookHotelRequest(BookHotel bookHotel){
      Builder builder = newBuilder();

      boolean result;
      Random r = new Random();
      if (r.nextInt()%2==0)
          result = true;
      else
          result = false;

      ResponseBookHotel responseBookHotel = ResponseBookHotel.newBuilder().setResult(result).build();


      builder.setBookRequest(responseBookHotel);
      return builder;
  }

  private Builder handleGetRoomListRequest(int hotel_id){
      Builder builder = newBuilder();

      List<Room> rooms = getRooms(hotel_id);

      System.out.println("size is " + rooms.size());

      ResponseGetRoomList responseGetHotelList = ResponseGetRoomList.newBuilder().addAllRooms(rooms).build();

      System.out.println(responseGetHotelList.getRoomsCount());

      builder.setRoomsRequest(responseGetHotelList);
      return builder;
  }

  private List<Room> getRooms(int hotel_id){
      List<Room> roomsList_1 = new ArrayList<>();
      List<Room> roomsList_2 = new ArrayList<>();
      List<Room> roomsList_3 = new ArrayList<>();
      Room room_1 = Room.newBuilder().setPrice(150).setType(
              Room_type.four
      ).build();
      Room room_2 = Room.newBuilder().setPrice(200).setType(
              Room_type.third
      ).build();
      Room room_3 = Room.newBuilder().setPrice(225).setType(
              Room_type.four
      ).build();
      Room room_4 = Room.newBuilder().setPrice(250).setType(
              Room_type.first
      ).build();
      roomsList_1.add(room_1);
      roomsList_1.add(room_2);

      roomsList_2.add(room_2);
      roomsList_2.add(room_3);

      roomsList_3.add(room_3);
      roomsList_3.add(room_4);

      switch (hotel_id){
          case 0: return roomsList_1;
          case 1: return roomsList_2;
          case 2: return roomsList_3;
          default: return new ArrayList<>();
      }
  }

  private List<Hotel> createHotels(){
      List<Hotel> hotels = new ArrayList<>();
      Hotel hotel_1 = Hotel.newBuilder().setName("hotel_1").setMaxPrice(100).setMinPrice(50).setHotelId(0).build();
      Hotel hotel_2 = Hotel.newBuilder().setName("hotel_2").setMaxPrice(1000).setMinPrice(500).setHotelId(1).build();
      Hotel hotel_3 = Hotel.newBuilder().setName("hotel_3").setMaxPrice(10000).setMinPrice(5000).setHotelId(2).build();
      hotels.add(hotel_1);
      hotels.add(hotel_2);
      hotels.add(hotel_3);

      return hotels;
  }

  
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
  }

}
